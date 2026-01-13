package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.*;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import edu.mtisw.payrollbackend.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoanService {
    @Autowired
    LoanRepository loanRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ToolRepository toolRepository;

    @Autowired
    ToolService toolService;

    @Autowired
    KardexRegisterRepository kardexRegisterRepository;

    @Autowired
    TariffService tariffService;

    public List<LoanEntity> getLoans() {
        return loanRepository.findAll();
    }

    public LoanEntity saveLoan(LoanEntity loan) throws ResponseStatusException {
        Long clientId = loan.getClientId();
        if (clientId == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
        }
        ClientEntity client = clientRepository.findById(clientId).get();

        Long toolId = loan.getToolId();
        if (toolId == null) {
            throw new IllegalArgumentException("El ID de la herramienta no puede ser nulo.");
        }
        ToolEntity tool = toolRepository.findById(toolId).get();

        if (client.isRestricted()) { // Cliente restringido
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El cliente" + client.getName() + " está restringido");
        } else if (client.getLoans().size() == 5) { // Cliente con 5 prestamos activos
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente " + client.getName() + " tiene 5 prestamos vigentes.");
        } else if (isSameTool(client, tool.getId())) { // Cliente ya tiene esta herramienta prestada
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cliente " + client.getName() + " ya tiene prestada esta herramienta.");
        } else if (toolService.getStock(tool.getName()) < 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay stock disponible de la herramienta " + tool.getName() + ".");
        }
        switch(tool.getStatus()) {
            case 0:
                throw new ResponseStatusException(HttpStatus.GONE, "La herramienta " + tool.getName() + " fué dada de baja.");
            case 1:
                throw new ResponseStatusException(HttpStatus.LOCKED, "La herramienta " + tool.getName() + " está en reparación.");
            case 2:
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La herramienta " + tool.getName() + " ya está prestada.");
            case 3:
                break;
        }
        loan.setToolId(tool.getId());
        loan.setToolName(tool.getName());

        TariffEntity tariff = tariffService.getTariff();
        loan.setTariffPerDay(tariff.getDailyTariff());
        loan.setTotalTariff(0L);
        loan.setDelayTariff(tariff.getDelayTariff());
        loan.setDelayFine(0L);

        LoanEntity savedLoan = loanRepository.save(loan);

        // Agregar datos del préstamo a la lista de préstamos del cliente
        LoanData data = createData(savedLoan, "Vigente");
        List<LoanData> loanList = client.getLoans();
        loanList.add(data);
        client.setLoans(loanList);
        registerLoanMovement(savedLoan, client, tool, "Prestamo");
        clientRepository.save(client);

        LocalDate now = LocalDate.now();
        String nowString = now.toString().split("T")[0];
        if (nowString.equals(savedLoan.getDateStart())) {
            tool.setStatus(2);
            toolRepository.save(tool);
        }

        return savedLoan;
    }

    public LoanEntity updateLoan(LoanEntity loan) {
        if (!loan.isActive()){
            // Eliminar prestamo de la lista de prestamos activos del cliente
            ClientEntity client = clientRepository.findById(loan.getClientId()).get();
            List<LoanData> clientLoans = client.getLoans();
            deleteLoanData(clientLoans, loan.getId());
            client.setLoans(clientLoans);

            // Calcular multa por atraso si es necesario
            if (loan.isDelayed()){
                Long daysLate = calculateDaysDiff(loan.getDateLimit(), loan.getDateReturn());
                Long fine = calculateFine(daysLate, loan.getTariffPerDay());
                loan.setDelayFine(fine);
                client.setFine(client.getFine() + fine);
            }

            clientRepository.save(client);

            LoanEntity updateLoan = loanRepository.save(loan);

            // Actualizar herramienta
            ToolEntity tool = toolRepository.findById(loan.getToolId()).get();
            List<LoanData> toolHistory = tool.getHistory();
            LoanData updatedLoanData = createData(updateLoan, "Devuelto");
            toolHistory.add(updatedLoanData);
            tool.setHistory(toolHistory);
            if (updateLoan.isToolGotDamaged()) tool.setStatus(1);
            else tool.setStatus(3);
            toolRepository.save(tool);

            // Registrar movimiento en kardex
            registerLoanMovement(loan, client, toolRepository.findById(loan.getToolId()).get(), "Devolución");

            return updateLoan;

        } else {
            return loanRepository.save(loan);
        }
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * *")
    public void updateActiveLoans(){ // Metodo automatico llamado cada dia a las 00:00:00 o cuando bakcend inicia
        LocalDate now = LocalDate.now();
        List<LoanEntity> loans = loanRepository.findByIsActiveTrueAndIsDelayedFalse(); // Obtener los prestamos activos
        for (LoanEntity loan : loans){
            LocalDate limit = LocalDate.parse(loan.getDateLimit());
            LocalDate start = LocalDate.parse(loan.getDateStart());
            if(now.isAfter(limit)){ // Si el prestamo ha pasado su fecha de límite
                loan.setDelayed(true); // Se marca como atrasado
                ClientEntity client = clientRepository.findById(loan.getClientId()).get(); // Se restringe al cliente
                client.setRestricted(true);
                List<LoanData> clientLoans = client.getLoans();
                deleteLoanData(clientLoans, loan.getId());
                LoanData data = createData(loan, "Atrasado");
                clientLoans.add(data);
                client.setLoans(clientLoans);
                clientRepository.save(client);
            } else if (now.isEqual(limit) || (now.isBefore(limit) && now.isAfter(start))) { // Si el prestamo esta en su periodo activo
                Long actualTotal = loan.getTotalTariff();
                loan.setTotalTariff(actualTotal + loan.getTariffPerDay()); // Se actualiza la tarifa total
            }
            updateLoan(loan); // Actualizar el prestamo en la base de datos
        }
    }

    private boolean isSameTool(ClientEntity client, Long toolId) {
        ToolEntity tool = toolRepository.findById(toolId).get();
        for (LoanData loanData : client.getLoans()) {
            LoanEntity loan = loanRepository.findById(loanData.getLoanID()).get();
            if (tool.getName().equals(loan.getToolName()) && (!toolId.equals(loan.getToolId()))) {
                return true;
            }
        }
        return false;
    }

    public LoanEntity getLoanById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del prestamo no puede ser nulo.");
        }
        return loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prestamo no encontrado con ID: " + id));
    }

    public boolean deleteLoan(Long id) throws Exception {
        try {
            LoanEntity loan = loanRepository.findById(id).get();
            if (loan.isActive()) {
                ClientEntity client = clientRepository.findById(loan.getClientId()).get();
                List<LoanData> clientLoans = client.getLoans();
                deleteLoanData(clientLoans, id);
                client.setLoans(clientLoans);
                clientRepository.save(client);
            }
            loanRepository.delete(loan);
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el prestamo: " + e.getMessage(), e);
        }
    }

    private Long calculateDaysDiff(String dateLimit, String dateReturn) {
        dateReturn = dateReturn.split("T")[0];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(dateLimit, formatter);
        LocalDate endDate = LocalDate.parse(dateReturn, formatter);
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    private Long calculateFine(Long daysLate, Long price) {
        return daysLate * price;
    }

    private void registerLoanMovement(LoanEntity loan, ClientEntity client, ToolEntity tool, String movement){
        KardexRegisterEntity newRegister = new KardexRegisterEntity();
        newRegister.setToolId(tool.getId());
        newRegister.setToolName(tool.getName());
        newRegister.setMovement(movement);
        newRegister.setTypeRelated(2);
        LocalDate date = LocalDate.now();
        newRegister.setDate(date);
        newRegister.setClientId(client.getId());
        newRegister.setClientName(client.getName());
        newRegister.setLoanId(loan.getId());
        kardexRegisterRepository.save(newRegister);
    }

    public List<LoanData> getActiveDelayedLoansData(boolean isDelayed) {
        List<LoanData> loanDataList = new ArrayList<>();
        List<LoanEntity> loans;
        if (isDelayed) {
            loans = loanRepository.findByIsActiveTrueAndIsDelayedTrue();
        } else {
            loans = loanRepository.findByIsActiveTrueAndIsDelayedFalse();
        }
        for (LoanEntity loan : loans) {
            LoanData loanData = new LoanData();
            loanData.setClientName(loan.getClientName());
            loanData.setToolName(loan.getToolName());
            loanData.setLoanDate(loan.getDateStart());
            loanData.setDueDate(loan.getDateLimit());
            loanData.setDataToolId(loan.getToolId());
            loanDataList.add(loanData);
        }
        return loanDataList;
    }

    private LoanData createData(LoanEntity loan, String status){
        LoanData data = new LoanData();
        data.setLoanID(loan.getId());
        data.setLoanDate(loan.getDateStart());
        data.setDueDate(loan.getDateLimit());
        data.setClientName(loan.getClientName());
        data.setToolName(loan.getToolName());
        data.setDataToolId(loan.getToolId());
        if (loan.getDateReturn() != null && !loan.getDateReturn().isEmpty()) {
            data.setReturnDate(loan.getDateReturn());
        }
        data.setStatus(status);
        return data;
    }

    private void deleteLoanData(List<LoanData> loanDataList, Long id){
        Iterator<LoanData> iterator = loanDataList.iterator();
        while (iterator.hasNext()) {
            LoanData loanData = iterator.next();
            if (loanData.getLoanID().equals(id)) {
                iterator.remove();
                break;
            }
        }
    }
}