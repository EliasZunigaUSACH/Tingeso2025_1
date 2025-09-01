package edu.mtisw.payrollbackend.services;

import ch.qos.logback.core.net.server.Client;
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
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    @Autowired
    LoanRepository loanRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ToolRepository toolRepository;

    @Autowired
    ClientService clientService;
    @Autowired
    private ToolService toolService;

    public ArrayList<LoanEntity> getLoans(){
        return (ArrayList<LoanEntity>) loanRepository.findAll();
    }

    public LoanEntity saveLoan(LoanEntity loan, Long clientId) {
        ClientEntity client = clientRepository.findById(clientId).get();
        ToolEntity tool = toolRepository.findById(loan.getToolId()).get();
        if (client.getStatus() == 0){  // Si el cliente está restringido
            throw new IllegalArgumentException("El cliente " + client.getName() + " está restringida, no puede realizar préstamos.");
        } else if (client.getLoans().size() == 5){ // Si el cliente ya tiene 5 prestamos activos
            throw new IllegalArgumentException("El cliente " + client.getName() + " solo puede tener 5 prestamos activos máximo.");
        } else if (isSameTool(client, loan.getToolId())){ // Si el cliente ya tiene prestado esta herramienta
            throw new IllegalArgumentException("El cliente " + client.getName() + " ya tiene prestado esta herrameinta.");
        } else if(toolService.getStock(tool.getName()) < 1) { // Si no hay herramientas disponibles
            throw new IllegalArgumentException("No hay stock de esta herramienta");
        } else {
            return loanRepository.save(loan);
        }
    }

/*
    private boolean checkStockAvaliable(String toolName){
        List<ToolEntity> tools = toolRepository.findByName(toolName);
        if (tools.isEmpty()){ // Si no hay herramientas con ese nombre
            return false;
        }
        for (ToolEntity tool : tools) { // Revisamos si hay herramientas disponibles
            if (tool.getStatus() == 3){ // Si hay una herramienta disponible
                return true;
            }
        }
        return false;
    }
*/

    private boolean isSameTool(ClientEntity client, Long toolId) {
        ToolEntity tool = toolRepository.findById(toolId).get();
        for (Long loanId : client.getLoans()) {
            LoanEntity loan = loanRepository.findById(loanId).get();
            if (tool.getName().equals(loan.getToolName())) {
                return true;
            }
        }
        return false;
    }

    public LoanEntity getLoanById(Long id){
        return loanRepository.findById(id).get();
    }

    public boolean deleteLoan(Long id) throws Exception {
        try {
            loanRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el préstamo: " + e.getMessage(), e);
        }
    }

    private Long calculateDaysDiff(String dateLimit, String dateReturn) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(dateLimit, formatter);
        LocalDate endDate = LocalDate.parse(dateReturn, formatter);
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public LoanEntity processReturn(LoanEntity loan) throws Exception {
        ClientEntity client = clientRepository.findById(loan.getClientId()).get();
        client.getLoans().remove(loan.getId());
        ToolEntity tool = toolRepository.findById(loan.getToolId()).get();
        if (tool.getStatus() == 0) {
            client.setFine(client.getFine() + tool.getPrice());
            client.setStatus(0);
        }
        Long daysDiff = calculateDaysDiff(loan.getDateLimit(), loan.getDateReturn());
        if (daysDiff < 0L) {
            throw new Exception("La herramienta se ha devuelto antes de la fecha de entrega, no hay devolución de dinero");
        } else if (daysDiff > 0L) {
            Long fine = loan.getPrice() * calculateDaysDiff(loan.getDateLimit(), loan.getDateReturn());
            client.setFine(client.getFine() + fine);
        }
        clientService.updateClient(client);
        return loanRepository.save(loan);
    }
}