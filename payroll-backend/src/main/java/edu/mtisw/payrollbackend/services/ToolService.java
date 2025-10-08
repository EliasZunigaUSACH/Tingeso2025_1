package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import edu.mtisw.payrollbackend.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.tools.Tool;
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

    public ToolEntity saveTool(ToolEntity tool) {
         ArrayList<Long> LoanIds = new ArrayList<>();
         tool.setLoansIds(LoanIds);
         return toolRepository.save(tool);
     }

     public List<ToolEntity> getAllTools(){
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

     public List<ToolEntity> getTop10Tools(){
         List<ToolEntity> tools = toolRepository.findAll();
         List<ToolEntity> top10 = new ArrayList<>();
         tools.sort((t1, t2) -> Integer.compare(t2.getLoansIds().size(), t1.getLoansIds().size()));
         for (int i = 0; i < 10; i++) {
             if(i < tools.size()){
                 top10.add(tools.get(i));
             }
         }
         return top10;
     }

     public ToolEntity updateTool(ToolEntity tool) {
         ToolEntity updatedTool = toolRepository.save(tool);
         if (updatedTool.getStatus() == 0) {
             Long price = updatedTool.getPrice();
             List<Long> loansIds = updatedTool.getLoansIds();
             Long lastID = loansIds.get(loansIds.size() - 1);
             LoanEntity lastLoan = loanRepository.findById(lastID).get();
             Long clientID = lastLoan.getClientId();
             ClientEntity client = clientRepository.findById(clientID).get();
             client.setFine(client.getFine() + price);
             clientService.updateClient(client);
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
}