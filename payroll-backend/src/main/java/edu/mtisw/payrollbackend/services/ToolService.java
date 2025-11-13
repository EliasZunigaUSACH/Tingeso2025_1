package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.*;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import edu.mtisw.payrollbackend.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ToolService {
    @Autowired
    ToolRepository toolRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private KardexRegisterRepository kardexRegisterRepository;

    public ToolEntity saveTool(ToolEntity tool) {
         ArrayList<LoanData> Loans = new ArrayList<>();
         tool.setHistory(Loans);
         registerToolMovement(tool, "Registro de herramienta");
         return toolRepository.save(tool);
    }

    public List<ToolEntity> getAllTools() {
        return toolRepository.findAll();
    }

    public ToolEntity getToolById(Long id){
          return toolRepository.findById(id).get();
    }

    public List<ToolEntity> getToolsByStatus(int status){
          return toolRepository.findByStatus(status);
    }

    public List<ToolEntity> getToolsByCategory(String category){
          return toolRepository.findByCategory(category);
    }

    public List<String> getTop10Tools(){
         List<ToolEntity> tools = toolRepository.findAll();
         List<ToolEntity> top10 = new ArrayList<>();
         tools.sort((t1, t2) -> Integer.compare(t2.getHistory().size(), t1.getHistory().size()));
         for (int i = 0; i < 10; i++) {
             if(i < tools.size()){
                 top10.add(tools.get(i));
             }
         }
         List<String> top10String = new ArrayList<>();
         for (ToolEntity tool : top10) {
             top10String.add(tool.getName() + " (" + tool.getId() + ")" + " - " + tool.getHistory().size() + " préstamos");
         }
         return top10String;
    }

    public ToolEntity updateTool(ToolEntity tool) {
         ToolEntity toolOld = toolRepository.findById(tool.getId()).get();
         ToolEntity updatedTool = toolRepository.save(tool);
         if (updatedTool.getStatus() == 0 && !tool.getHistory().isEmpty()) {
             Long price = updatedTool.getPrice();
             List<LoanData> history = updatedTool.getHistory();
             Long lastID = history.get(history.size() - 1).getLoanID();
             LoanEntity lastLoan = loanRepository.findById(lastID).get();
             Long clientID = lastLoan.getClientId();
             ClientEntity client = clientRepository.findById(clientID).get();
             if (lastLoan.isDelayed()) client.setFine(client.getFine() - lastLoan.getDelayFine() + price);
             else client.setFine(client.getFine() + price);
             clientService.updateClient(client);
             registerToolMovement(updatedTool, "Baja de herramienta");
         } else if (updatedTool.getStatus() == 3 && toolOld.getStatus() == 2) {
             registerToolMovement(updatedTool, "Reparacion");
         } else {
             registerToolMovement(updatedTool, "Actualizacion de herramienta");
         }
         return updatedTool;
    }

    public boolean deleteTool(Long id) throws Exception {
         try{
             toolRepository.deleteById(id);
             return true;
         } catch (Exception e){
             throw new Exception(e.getMessage());
         }
    }

    public int getStock(String name){
         return toolRepository.findByNameAndStatus(name, 3).size();
    }

    private void registerToolMovement(ToolEntity tool, String movement){
        KardexRegisterEntity newRegister = new KardexRegisterEntity();
        newRegister.setToolId(tool.getId());
        newRegister.setToolName(tool.getName());
        newRegister.setMovement(movement);
        newRegister.setTypeRelated(1);
        LocalDate date = LocalDate.now();
        newRegister.setDate(date);
        newRegister.setClientId(null);
        newRegister.setClientName(null);
        newRegister.setLoanId(null);
        kardexRegisterRepository.save(newRegister);
    }
}