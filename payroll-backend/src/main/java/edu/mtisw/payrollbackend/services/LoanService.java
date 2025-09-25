package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
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
    ClientService clientService;

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
        ClientEntity client = clientService.getClientById(clientId);

        Long toolId = loan.getToolId();
        if (toolId == null) {
            throw new IllegalArgumentException("El ID de la herramienta no puede ser nulo.");
        }
        ToolEntity tool = toolService.getToolById(toolId);

        if (client.getStatus() == 0) { // Cliente restringido
            throw new IllegalArgumentException("El cliente " + client.getName() + " está restringida, no puede realizar préstamos.");
        } else if (client.getLoans().size() == 5) { // Cliente con 5 préstamos activos
            throw new IllegalArgumentException("El cliente " + client.getName() + " solo puede tener 5 préstamos activos máximo.");
        } else if (isSameTool(client, tool.getId())) { // Cliente ya tiene esta herramienta prestada
            throw new IllegalArgumentException("El cliente " + client.getName() + " ya tiene prestada esta herramienta.");
        } else if (toolService.getStock(tool.getName()) < 1) {
            throw new IllegalArgumentException("No hay stock de esta herramienta");
        }

        List<Long> history = client.getLoans();
        history.add(loan.getId());
        client.setLoans(history);
        clientService.updateClient(client);

        return loanRepository.save(loan);
    }

    public LoanEntity updateLoan(LoanEntity loan) {
        if (loan.getStatus() == 0) {
            ToolEntity tool = toolService.getToolById(loan.getToolId());
            List<Long> history = tool.getLoansIds();
            history.add(loan.getId());
            tool.setLoansIds(history);

            ClientEntity client = clientService.getClientById(loan.getClientId());
            List<Long> clientLoans = client.getLoans();
            clientLoans.remove(loan.getId());
            client.setLoans(clientLoans);

            if (tool.getStatus() == 0) {
                client.setFine(client.getFine() + tool.getPrice());
                client.setStatus(0);
            } else {
                Long daysDiff = calculateDaysDiff(loan.getDateLimit(), loan.getDateReturn());
                if (daysDiff > 0L) {
                    Long fine = calculateFine(daysDiff, tool.getPrice());
                    client.setFine(client.getFine() + fine);
                    loan.setIsDelayedReturn(1);
                }
            }
            toolService.updateTool(tool);
            clientService.updateClient(client);
        }
        return loanRepository.save(loan);
    }

    private boolean isSameTool(ClientEntity client, Long toolId) {
        if (toolId == null) {
            throw new IllegalArgumentException("El ID de la herramienta no puede ser nulo.");
        }

        ToolEntity tool = toolService.getToolById(toolId);

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