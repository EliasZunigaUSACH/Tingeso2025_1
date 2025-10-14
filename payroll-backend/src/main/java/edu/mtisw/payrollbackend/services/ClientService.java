package edu.mtisw.payrollbackend.services;

import ch.qos.logback.core.net.server.Client;
import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientService {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    LoanRepository loanRepository;

    public ArrayList<ClientEntity> getClients(){
        return (ArrayList<ClientEntity>) clientRepository.findAll();
    }

    public ClientEntity saveClient(ClientEntity user){
        ArrayList<Long> LoanIds = new ArrayList<>();
        user.setLoans(LoanIds);
        user.setFine(0L);
        user.setStatus(1);
        return clientRepository.save(user);
    }

    public ClientEntity getClientById(Long id){
        return clientRepository.findById(id).get();
    }

    public ClientEntity updateClient(ClientEntity user) {
        if (user.getFine() > 0L) user.setStatus(0);
        else user.setStatus(1);
        return clientRepository.save(user);
    }

    public boolean deleteClient(Long id) throws Exception {
        try{
            clientRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<String> getClientsWithDelayedLoans(){
        List<ClientEntity> clients = clientRepository.findAll();
        clients.removeIf(client -> !detectDelayedLoans(client));
        List<String> clientsWithDelayedLoans = new ArrayList<>();
        for (ClientEntity client : clients) {
            clientsWithDelayedLoans.add(client.getName() + " con " + countDelayedLoans(client) + " préstamos retrasados");
        }
        return clientsWithDelayedLoans;
    }

    private boolean detectDelayedLoans(ClientEntity client) {
        if (client.getLoans().isEmpty()) return false;
        else {
            for (Long loanId : client.getLoans()) {
                LoanEntity loan = loanRepository.findById(loanId).get();
                if (loan.getStatus() == 2) return true;
            }
            return false;
        }
    }
     private int countDelayedLoans(ClientEntity client) {
        if (client.getLoans().isEmpty()) return 0;
        else {
            int count = 0;
            for (Long loanId : client.getLoans()) {
                LoanEntity loan = loanRepository.findById(loanId).get();
                if (loan.getStatus() == 2) count++;
            }
            return count;
        }
     }
}