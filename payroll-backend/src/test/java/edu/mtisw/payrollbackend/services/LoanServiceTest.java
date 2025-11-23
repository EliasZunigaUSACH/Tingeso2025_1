package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.entities.LoanData;
import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import edu.mtisw.payrollbackend.repositories.ToolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private ToolService toolService;

    @Mock
    private KardexRegisterRepository kardexRegisterRepository;

    @Mock
    private TariffService tariffService;

    @InjectMocks
    private LoanService loanService;

    private ClientEntity client;
    private ToolEntity tool;
    private TariffEntity tariffBase;

    @BeforeEach
    void setUp() {
        client = new ClientEntity();
        client.setId(1L);
        client.setName("Cliente 1");
        client.setRestricted(false);
        client.setLoans(new ArrayList<>());
        client.setFine(0L);

        tool = new ToolEntity();
        tool.setId(10L);
        tool.setName("Martillo");
        tool.setHistory(new ArrayList<>());
        tool.setStatus(3);

        tariffBase = new TariffEntity();
        tariffBase.setDailyTariff(1_000L);
        tariffBase.setDelayTariff(2_000L);
    }

    // -------------------------------------------------------------------------
    // getLoans()  -  3 ejemplos
    // -------------------------------------------------------------------------

    @Test
    void getLoans_debeRetornarListaVaciaCuandoNoHayPrestamos() {
        // given
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<LoanEntity> result = loanService.getLoans();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(loanRepository).findAll();
    }

    @Test
    void getLoans_debeRetornarListaConUnPrestamo() {
        // given
        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        when(loanRepository.findAll()).thenReturn(Collections.singletonList(loan));

        // when
        List<LoanEntity> result = loanService.getLoans();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(loanRepository).findAll();
    }

    @Test
    void getLoans_debeRetornarListaConVariosPrestamos() {
        // given
        LoanEntity loan1 = new LoanEntity();
        loan1.setId(1L);
        LoanEntity loan2 = new LoanEntity();
        loan2.setId(2L);

        when(loanRepository.findAll()).thenReturn(Arrays.asList(loan1, loan2));

        // when
        List<LoanEntity> result = loanService.getLoans();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(loanRepository).findAll();
    }

    // -------------------------------------------------------------------------
    // saveLoan()  -  3 ejemplos
    // -------------------------------------------------------------------------

    @Test
    void saveLoan_debeGuardarPrestamoCorrectamente() {
        LoanEntity loan = new LoanEntity();
        loan.setId(100L);
        loan.setClientId(1L);
        loan.setClientName("Cliente 1");
        loan.setToolId(10L);
        loan.setDateStart(LocalDate.now().toString());
        loan.setDateLimit(LocalDate.now().plusDays(3).toString());
        loan.setDateReturn("");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(toolRepository.findById(10L)).thenReturn(Optional.of(tool));
        when(toolService.getStock("Martillo")).thenReturn(5);
        when(tariffService.getTariff()).thenReturn(tariffBase);
        when(loanRepository.save(any(LoanEntity.class))).thenAnswer(invocation -> {
            LoanEntity arg = invocation.getArgument(0);
            // simulamos que la BD asigna el mismo id
            arg.setId(100L);
            return arg;
        });

        LoanEntity saved = loanService.saveLoan(loan);

        assertEquals(100L, saved.getId());
        assertEquals("Martillo", saved.getToolName());
        assertEquals(1_000L, saved.getTariffPerDay());
        assertEquals(0L, saved.getTotalTariff());
        assertEquals(2_000L, saved.getDelayTariff());
        assertEquals(0L, saved.getDelayFine());

        // Verifica que se registra el movimiento en kardex
        verify(kardexRegisterRepository).save(any(KardexRegisterEntity.class));
        // Verifica que se añadió LoanData al cliente
        verify(clientRepository, times(1)).save(any(ClientEntity.class));
    }

    @Test
    void saveLoan_debeLanzarExcepcionCuandoClientIdEsNulo() {
        LoanEntity loan = new LoanEntity();
        loan.setClientId(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.saveLoan(loan)
        );

        assertEquals("El ID del cliente no puede ser nulo.", ex.getMessage());
        verifyNoInteractions(clientRepository, toolRepository, toolService, tariffService);
    }

    @Test
    void saveLoan_debeLanzarExcepcionCuandoClienteRestringido() {
        LoanEntity loan = new LoanEntity();
        loan.setClientId(1L);
        loan.setToolId(10L);

        client.setRestricted(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(toolRepository.findById(10L)).thenReturn(Optional.of(tool));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.saveLoan(loan)
        );

        assertTrue(ex.getMessage().contains("está restringido"));
        verify(loanRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // updateLoan()  -  3 ejemplos
    // -------------------------------------------------------------------------

    @Test
    void updateLoan_debeActualizarPrestamoActivoSinCambiosEspeciales() {
        LoanEntity loan = new LoanEntity();
        loan.setId(200L);
        loan.setActive(true);

        when(loanRepository.save(loan)).thenReturn(loan);

        LoanEntity result = loanService.updateLoan(loan);

        assertSame(loan, result);
        verify(loanRepository).save(loan);
        verifyNoInteractions(clientRepository, toolRepository);
    }

    @Test
    void updateLoan_debeActualizarPrestamoInactivoSinAtraso() {
        // Prestamo devuelto sin atraso
        LoanEntity loan = new LoanEntity();
        loan.setId(201L);
        loan.setClientId(1L);
        loan.setToolId(10L);
        loan.setActive(false);
        loan.setDelayed(false);
        loan.setToolGotDamaged(false);
        loan.setTariffPerDay(1_000L);
        loan.setDelayFine(0L);
        loan.setDateReturn(LocalDate.now().toString());

        client.getLoans().add(crearLoanData(201L));
        tool.setHistory(new ArrayList<>());

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(toolRepository.findById(10L)).thenReturn(Optional.of(tool));

        LoanEntity result = loanService.updateLoan(loan);

        assertFalse(result.isActive());
        assertEquals(0L, result.getDelayFine());
        assertEquals(3, tool.getStatus()); // no dañada -> disponible
        verify(clientRepository).save(client);
        verify(toolRepository, times(1)).save(tool);
    }

    @Test
    void updateLoan_debeCalcularMultaCuandoPrestamoInactivoYAtrasado() {
        LoanEntity loan = new LoanEntity();
        loan.setId(202L);
        loan.setClientId(1L);
        loan.setToolId(10L);
        loan.setActive(false);
        loan.setDelayed(true);
        loan.setToolGotDamaged(true); // herramienta dañada
        loan.setTariffPerDay(2_000L);
        loan.setDateLimit(LocalDate.now().minusDays(5).toString());
        loan.setDateReturn(LocalDate.now().toString());

        client.getLoans().add(crearLoanData(202L));
        tool.setHistory(new ArrayList<>());

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(toolRepository.findById(10L)).thenReturn(Optional.of(tool));

        LoanEntity result = loanService.updateLoan(loan);

        // 5 días de atraso * 2.000 = 10.000
        assertEquals(10_000L, result.getDelayFine());
        assertEquals(10_000L, client.getFine());
        assertEquals(1, tool.getStatus()); // dañada
        verify(clientRepository).save(client);
        verify(toolRepository, times(1)).save(tool);
    }

    @Test
    void getLoanById_debeRetornarPrestamoCuandoExiste() {
        LoanEntity loan = new LoanEntity();
        loan.setId(300L);
        when(loanRepository.findById(300L)).thenReturn(Optional.of(loan));

        LoanEntity result = loanService.getLoanById(300L);

        assertEquals(300L, result.getId());
        verify(loanRepository).findById(300L);
    }

    @Test
    void getLoanById_debeLanzarExcepcionCuandoIdEsNulo() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.getLoanById(null)
        );

        assertEquals("El ID del prestamo no puede ser nulo.", ex.getMessage());
        verifyNoInteractions(loanRepository);
    }

    @Test
    void getLoanById_debeLanzarExcepcionCuandoNoExistePrestamo() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.getLoanById(999L)
        );

        assertTrue(ex.getMessage().contains("Prestamo no encontrado con ID: 999"));
    }

    // -------------------------------------------------------------------------
    // deleteLoan()  -  3 ejemplos
    // -------------------------------------------------------------------------

    @Test
    void deleteLoan_debeEliminarPrestamoActivoYActualizarCliente() throws Exception {
        LoanEntity loan = new LoanEntity();
        loan.setId(400L);
        loan.setClientId(1L);
        loan.setActive(true);

        LoanData data = crearLoanData(400L);
        client.getLoans().add(data);

        when(loanRepository.findById(400L)).thenReturn(Optional.of(loan));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        boolean result = loanService.deleteLoan(400L);

        assertTrue(result);
        assertTrue(client.getLoans().isEmpty());
        verify(clientRepository).save(client);
        verify(loanRepository).delete(loan);
    }

    @Test
    void deleteLoan_debeEliminarPrestamoInactivoSinModificarCliente() throws Exception {
        LoanEntity loan = new LoanEntity();
        loan.setId(401L);
        loan.setClientId(1L);
        loan.setActive(false);

        when(loanRepository.findById(401L)).thenReturn(Optional.of(loan));

        boolean result = loanService.deleteLoan(401L);

        assertTrue(result);
        verify(clientRepository, never()).save(any());
        verify(loanRepository).delete(loan);
    }

    @Test
    void deleteLoan_debeLanzarExceptionCuandoOcurreErrorEnRepositorio() {
        when(loanRepository.findById(500L)).thenThrow(new RuntimeException("Falla DB"));

        Exception ex = assertThrows(
                Exception.class,
                () -> loanService.deleteLoan(500L)
        );

        assertTrue(ex.getMessage().contains("Error al eliminar el prestamo"));
    }

    // -------------------------------------------------------------------------
    // getActiveDelayedLoansData()  -  3 ejemplos
    // -------------------------------------------------------------------------

    @Test
    void getActiveDelayedLoansData_debeRetornarDatosPrestamosAtrasados() {
        LoanEntity loan = new LoanEntity();
        loan.setId(600L);
        loan.setActive(true);
        loan.setDelayed(true);
        loan.setClientName("Cliente 1");
        loan.setToolName("Martillo");
        loan.setDateStart("2024-01-01");
        loan.setDateLimit("2024-01-10");
        loan.setToolId(10L);

        when(loanRepository.findByIsActiveTrueAndIsDelayedTrue())
                .thenReturn(Collections.singletonList(loan));

        List<LoanData> data = loanService.getActiveDelayedLoansData(true);

        assertEquals(1, data.size());
        LoanData d = data.get(0);
        assertEquals("Cliente 1", d.getClientName());
        assertEquals("Martillo", d.getToolName());
        assertEquals("2024-01-01", d.getLoanDate());
        assertEquals("2024-01-10", d.getDueDate());
        assertEquals(10L, d.getDataToolId());
    }

    @Test
    void getActiveDelayedLoansData_debeRetornarDatosPrestamosActivosNoAtrasados() {
        LoanEntity loan1 = new LoanEntity();
        loan1.setId(601L);
        loan1.setActive(true);
        loan1.setDelayed(false);
        loan1.setClientName("Cliente 1");
        loan1.setToolName("Martillo");
        loan1.setDateStart("2024-02-01");
        loan1.setDateLimit("2024-02-10");
        loan1.setToolId(10L);

        LoanEntity loan2 = new LoanEntity();
        loan2.setId(602L);
        loan2.setActive(true);
        loan2.setDelayed(false);
        loan2.setClientName("Cliente 2");
        loan2.setToolName("Taladro");
        loan2.setDateStart("2024-02-05");
        loan2.setDateLimit("2024-02-15");
        loan2.setToolId(11L);

        when(loanRepository.findByIsActiveTrueAndIsDelayedFalse())
                .thenReturn(Arrays.asList(loan1, loan2));

        List<LoanData> data = loanService.getActiveDelayedLoansData(false);

        assertEquals(2, data.size());
        assertEquals("Cliente 1", data.get(0).getClientName());
        assertEquals("Cliente 2", data.get(1).getClientName());
    }

    @Test
    void getActiveDelayedLoansData_debeRetornarListaVaciaCuandoNoHayPrestamos() {
        when(loanRepository.findByIsActiveTrueAndIsDelayedTrue()).thenReturn(Collections.emptyList());
        when(loanRepository.findByIsActiveTrueAndIsDelayedFalse()).thenReturn(Collections.emptyList());

        List<LoanData> delayed = loanService.getActiveDelayedLoansData(true);
        List<LoanData> active = loanService.getActiveDelayedLoansData(false);

        assertTrue(delayed.isEmpty());
        assertTrue(active.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Métodos de ayuda para crear entidades de prueba
    // -------------------------------------------------------------------------

    private LoanData crearLoanData(Long loanId) {
        LoanData data = new LoanData();
        data.setLoanID(loanId);
        data.setClientName("Cliente 1");
        data.setToolName("Martillo");
        data.setLoanDate("2024-01-01");
        data.setDueDate("2024-01-10");
        data.setDataToolId(10L);
        return data;
    }
}
