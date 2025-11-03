package edu.mtisw.payrollbackend.services;

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

    public ClientEntity saveClient(ClientEntity client){
        ArrayList<Long> LoanIds = new ArrayList<>();
        client.setLoans(LoanIds);
        return clientRepository.save(client);
    }

    public ClientEntity getClientById(Long id){
        return clientRepository.findById(id).get();
    }

    public ClientEntity updateClient(ClientEntity client) {
        client.setRestricted((client.getFine() > 0L || detectDelayedLoans(client)));
        return clientRepository.save(client);
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
        List<Long> loanIds = client.getLoans();
        if (!loanIds.isEmpty())  {
            for (Long loanId : loanIds) {
                LoanEntity loan = loanRepository.findById(loanId).get();
                if (loan.isDelayed()) return true;
            }
        }
        return false;
    }

    private int countDelayedLoans(ClientEntity client) {
        List<Long> loanIds = client.getLoans();
        if (!loanIds.isEmpty())  {
            int count = 0;
            for (Long loanId : loanIds) {
                LoanEntity loan = loanRepository.findById(loanId).get();
                if (loan.isDelayed()) count++;
            }
            return count;
        }
        return 0;
    }
}