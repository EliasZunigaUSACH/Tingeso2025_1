package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.LoanData;
import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private LoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ============================
    // getClients()
    // ============================

    @Test
    void getClients_emptyList() {
        when(clientRepository.findAll()).thenReturn(new ArrayList<>());

        List<ClientEntity> result = clientService.getClients();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void getClients_ListWith1client() {
        ClientEntity client = buildClient(1L, "Juan");
        when(clientRepository.findAll()).thenReturn(List.of(client));

        List<ClientEntity> result = clientService.getClients();

        assertEquals(1, result.size());
        assertEquals(client, result.get(0));
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void getClients_ListWithClients() {
        ClientEntity c1 = buildClient(1L, "Juan");
        ClientEntity c2 = buildClient(2L, "Ana");
        ClientEntity c3 = buildClient(3L, "Pedro");
        when(clientRepository.findAll()).thenReturn(Arrays.asList(c1, c2, c3));

        List<ClientEntity> result = clientService.getClients();

        assertEquals(3, result.size());
        assertTrue(result.contains(c1));
        assertTrue(result.contains(c2));
        assertTrue(result.contains(c3));
        verify(clientRepository, times(1)).findAll();
    }

    // ============================
    // saveClient(ClientEntity client)
    // ============================

    @Test
    void saveClient_EmptyList() {
        ClientEntity client = buildClient(null, "Juan");
        client.setLoans(List.of(new LoanData())); // valor que debería ser sobrescrito

        when(clientRepository.save(any(ClientEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClientEntity result = clientService.saveClient(client);

        assertNotNull(result.getLoans());
        assertTrue(result.getLoans().isEmpty());
        verify(clientRepository, times(1)).save(result);
    }

    @Test
    void saveClient_SaveWithBasicData() {
        ClientEntity client = buildClient(null, "Ana");

        when(clientRepository.save(any(ClientEntity.class)))
                .thenAnswer(invocation -> {
                    ClientEntity c = invocation.getArgument(0);
                    c.setId(10L);
                    return c;
                });

        ClientEntity result = clientService.saveClient(client);

        assertEquals(10L, result.getId());
        assertEquals("Ana", result.getName());
        assertNotNull(result.getLoans());
        assertTrue(result.getLoans().isEmpty());
        verify(clientRepository, times(1)).save(result);
    }

    @Test
    void saveClient_AllowMultipleClients() {
        ClientEntity c1 = buildClient(null, "Juan");
        ClientEntity c2 = buildClient(null, "Ana");

        when(clientRepository.save(any(ClientEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClientEntity r1 = clientService.saveClient(c1);
        ClientEntity r2 = clientService.saveClient(c2);

        assertEquals("Juan", r1.getName());
        assertEquals("Ana", r2.getName());
        assertTrue(r1.getLoans().isEmpty());
        assertTrue(r2.getLoans().isEmpty());
        verify(clientRepository, times(2)).save(any(ClientEntity.class));
    }

    // ============================
    // getClientById(Long id)
    // ============================

    @Test
    void getClientById_ReturnExistentClient() {
        ClientEntity client = buildClient(1L, "Juan");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientEntity result = clientService.getClientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getName());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void getClientById_NonExistentClient() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> clientService.getClientById(999L));
        verify(clientRepository, times(1)).findById(999L);
    }

    @Test
    void getClientById_ClientsWithDifferentIDs() {
        ClientEntity c1 = buildClient(1L, "Juan");
        ClientEntity c2 = buildClient(2L, "Ana");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(clientRepository.findById(2L)).thenReturn(Optional.of(c2));

        ClientEntity r1 = clientService.getClientById(1L);
        ClientEntity r2 = clientService.getClientById(2L);

        assertEquals("Juan", r1.getName());
        assertEquals("Ana", r2.getName());
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).findById(2L);
    }

    // ============================
    // updateClient(ClientEntity client)
    // ============================

    @Test
    void updateClient_restrictClientWithFine() {
        ClientEntity client = buildClient(1L, "Juan");
        client.setFine(100L);  // multa > 0
        client.setLoans(new ArrayList<>()); // sin préstamos

        when(clientRepository.save(any(ClientEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClientEntity result = clientService.updateClient(client);

        assertTrue(result.isRestricted());
        verify(clientRepository, times(1)).save(result);
        verifyNoInteractions(loanRepository);
    }

    @Test
    void updateClient_RestrictClientWithDelayedLoan() {
        ClientEntity client = buildClient(1L, "Ana");
        client.setFine(0L); // sin multa
        client.setLoans(List.of(new LoanData(1L, "2024-01-01", "2024-01-10",
                "Ana", "Taladro", 1L, null, "ATRASADO")));

        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        loan.setDelayed(true);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(clientRepository.save(any(ClientEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClientEntity result = clientService.updateClient(client);

        assertTrue(result.isRestricted());
        verify(clientRepository, times(1)).save(result);
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    void updateClient_NoRestrictionByNoFineOrDelayedLoan() {
        ClientEntity client = buildClient(1L, "Pedro");
        client.setFine(0L);
        client.setLoans(List.of(new LoanData(1L, "2024-01-01", "2024-01-10",
                "Pedro", "Taladro", 1L, "2024-01-05", "DEVUELTO")));

        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        loan.setDelayed(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(clientRepository.save(any(ClientEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClientEntity result = clientService.updateClient(client);

        assertFalse(result.isRestricted());
        verify(clientRepository, times(1)).save(result);
        verify(loanRepository, times(1)).findById(1L);
    }

    // ============================
    // deleteClient(Long id)
    // ============================

    @Test
    void deleteClient_SuccesfulDeletion() throws Exception {
        doNothing().when(clientRepository).deleteById(1L);

        boolean result = clientService.deleteClient(1L);

        assertTrue(result);
        verify(clientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteClient_FailedRepositoryException() {
        doThrow(new RuntimeException("error"))
                .when(clientRepository).deleteById(1L);

        Exception ex = assertThrows(Exception.class, () -> clientService.deleteClient(1L));

        assertEquals("error", ex.getMessage());
        verify(clientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteClient_DeleteWithCorrectID() throws Exception {
        Long id = 5L;
        doNothing().when(clientRepository).deleteById(id);

        boolean result = clientService.deleteClient(id);

        assertTrue(result);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(clientRepository).deleteById(captor.capture());
        assertEquals(id, captor.getValue());
    }

    // ============================
    // getClientsWithDelayedLoans()
    // ============================

    @Test
    void getClientsWithDelayedLoans_EmptyClientList() {
        when(clientRepository.findAll()).thenReturn(new ArrayList<>());

        List<String> result = clientService.getClientsWithDelayedLoans();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clientRepository, times(1)).findAll();
        verifyNoInteractions(loanRepository);
    }

    @Test
    void getClientsWithDelayedLoans_ExcludeClientsWithDelayedLoans() {
        ClientEntity c1 = buildClient(1L, "Juan");
        c1.setLoans(List.of(new LoanData(1L, "2024-01-01", "2024-01-10",
                "Juan", "Taladro", 1L, "2024-01-05", "OK")));

        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        loan.setDelayed(false);

        when(clientRepository.findAll()).thenReturn(List.of(c1));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        List<String> result = clientService.getClientsWithDelayedLoans();

        assertTrue(result.isEmpty());
        verify(clientRepository, times(1)).findAll();
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    void getClientsWithDelayedLoans_ClientsWithDelayedLoans(){
        // Cliente 1: 2 préstamos, 1 atrasado
        ClientEntity c1 = buildClient(1L, "Juan");
        c1.setLoans(List.of(
                new LoanData(1L, "2024-01-01", "2024-01-10",
                        "Juan", "Taladro", 1L, null, "ATRASADO"),
                new LoanData(2L, "2024-01-01", "2024-01-10",
                        "Juan", "Martillo", 2L, "2024-01-05", "OK")
        ));

        // Cliente 2: 2 préstamos, 2 atrasados
        ClientEntity c2 = buildClient(2L, "Ana");
        c2.setLoans(List.of(
                new LoanData(3L, "2024-01-01", "2024-01-10",
                        "Ana", "Taladro", 1L, null, "ATRASADO"),
                new LoanData(4L, "2024-01-01", "2024-01-10",
                        "Ana", "Martillo", 2L, null, "ATRASADO")
        ));

        when(clientRepository.findAll()).thenReturn(List.of(c1, c2));

        LoanEntity loan1 = new LoanEntity(); loan1.setId(1L); loan1.setDelayed(true);
        LoanEntity loan2 = new LoanEntity(); loan2.setId(2L); loan2.setDelayed(false);
        LoanEntity loan3 = new LoanEntity(); loan3.setId(3L); loan3.setDelayed(true);
        LoanEntity loan4 = new LoanEntity(); loan4.setId(4L); loan4.setDelayed(true);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan1));
        when(loanRepository.findById(2L)).thenReturn(Optional.of(loan2));
        when(loanRepository.findById(3L)).thenReturn(Optional.of(loan3));
        when(loanRepository.findById(4L)).thenReturn(Optional.of(loan4));

        List<String> result = clientService.getClientsWithDelayedLoans();

        assertEquals(2, result.size());
        assertTrue(result.contains("Juan con 1 préstamos retrasados"));
        assertTrue(result.contains("Ana con 2 préstamos retrasados"));

        verify(clientRepository, times(1)).findAll();
        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).findById(2L);
        verify(loanRepository, times(1)).findById(3L);
        verify(loanRepository, times(1)).findById(4L);
    }

    // ============================
    // Métodos de ayuda
    // ============================

    private ClientEntity buildClient(Long id, String name) {
        ClientEntity client = new ClientEntity();
        client.setId(id);
        client.setName(name);
        client.setPhone("123456");
        client.setRut("11.111.111-1");
        client.setEmail(name.toLowerCase() + "@mail.com");
        client.setRestricted(false);
        client.setLoans(new ArrayList<>());
        client.setFine(0L);
        return client;
    }
}
