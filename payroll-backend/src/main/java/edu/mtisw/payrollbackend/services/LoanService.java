package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import edu.mtisw.payrollbackend.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ArrayList<LoanEntity> getLoans() {
        return (ArrayList<LoanEntity>) loanRepository.findAll();
    }

    public LoanEntity saveLoan(LoanEntity loan) {



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

        if (client.getStatus() == 0) { // Cliente restringido
            throw new IllegalArgumentException("El cliente " + client.getName() + " está restringida, no puede realizar préstamos.");
        } else if (client.getLoans().size() == 5) { // Cliente con 5 préstamos activos
            throw new IllegalArgumentException("El cliente " + client.getName() + " solo puede tener 5 préstamos activos máximo.");
        } else if (isSameTool(client, tool.getId())) { // Cliente ya tiene esta herramienta prestada
            throw new IllegalArgumentException("El cliente " + client.getName() + " ya tiene prestada esta herramienta.");
        } else if (toolService.getStock(tool.getName()) < 1) {
            throw new IllegalArgumentException("No hay stock de esta herramienta");
        }

        LoanEntity savedLoan = loanRepository.save(loan);

        List<Long> history = client.getLoans();
        history.add(savedLoan.getId());
        client.setLoans(history);
        clientRepository.save(client);

        return savedLoan;
    }

    public LoanEntity updateLoan(LoanEntity loan) {
        LoanEntity updatedLoan = loanRepository.save(loan);

        if (updatedLoan.getStatus() == 0) { // Estado del préstamo: terminado
            ToolEntity tool = toolRepository.findById(updatedLoan.getToolId()).get();
            if (tool == null) {
                throw new IllegalArgumentException("La herramienta asociada al préstamo no existe.");
            }

            // Obtener y actualizar la lista de préstamos en el historial
            List<Long> history = tool.getLoansIds();
            if (!history.contains(updatedLoan.getId())) { // Evitar duplicados en el historial
                history.add(updatedLoan.getId());
            }
            tool.setLoansIds(history); // Actualizar el historial de préstamos
            ToolEntity updatedTool = toolRepository.save(tool); // Guardar los cambios de la herramienta

            // Obtener y actualizar la lista de préstamos del cliente
            ClientEntity client = clientRepository.findById(updatedLoan.getClientId()).get();
            if (client == null) {
                throw new IllegalArgumentException("El cliente asociado al préstamo no existe.");
            }

            List<Long> clientLoans = client.getLoans();
            clientLoans.remove(updatedLoan.getId()); // Eliminar el préstamo de la lista activa del cliente
            client.setLoans(clientLoans);
                // Calcular la multa si el préstamo está retrasado
            Long daysDiff = calculateDaysDiff(updatedLoan.getDateLimit(), updatedLoan.getDateReturn());
            if (daysDiff > 0L) {
                Long fine = calculateFine(daysDiff, updatedLoan.getDelayTariff());
                client.setFine(client.getFine() + fine);
                client.setStatus(0);
                updatedLoan.setIsDelayedReturn(1); // Marcar el préstamo como devuelto con retraso
            }

            clientRepository.save(client); // Guardar los cambios del cliente
        }
        return updatedLoan; // Guardar los cambios del préstamo
    }

    private boolean isSameTool(ClientEntity client, Long toolId) {
        if (toolId == null) {
            throw new IllegalArgumentException("El ID de la herramienta no puede ser nulo.");
        }

        ToolEntity tool = toolRepository.findById(toolId).get();

        for (Long loanId : client.getLoans()) {
            if (loanId == null) {
                continue; // Ignorar IDs nulos en los préstamos
            }

            LoanEntity loan = loanRepository.findById(loanId)
                    .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado con ID: " + loanId));

            if (tool.getName().equals(loan.getToolName())) {
                return true;
            }
        }
        return false;
    }

    public LoanEntity getLoanById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del préstamo no puede ser nulo.");
        }
        return loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado con ID: " + id));
    }

    public boolean deleteLoan(Long id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("El ID del préstamo no puede ser nulo.");
        }

        try {
            loanRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el préstamo: " + e.getMessage(), e);
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
}