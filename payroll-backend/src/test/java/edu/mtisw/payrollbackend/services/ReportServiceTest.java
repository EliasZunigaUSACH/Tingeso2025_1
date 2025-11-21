package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.repositories.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ClientService clientService;

    @Mock
    private ToolService toolService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private ReportService reportService;

    private ReportEntity baseReport;

    @BeforeEach
    void setUp() {
        baseReport = new ReportEntity();
        baseReport.setId(1L);
        baseReport.setCreationDate(LocalDate.now().toString());
        baseReport.setActiveLoans(new ArrayList<>());
        baseReport.setDelayedLoans(new ArrayList<>());
        baseReport.setClientsWithDelayedLoans(new ArrayList<>());
        baseReport.setTopTools(new ArrayList<>());
    }

    // ============================
    // getReports()
    // ============================

    @Test
    void getReports_debeRetornarListaVaciaCuandoNoHayReportes() {
        when(reportRepository.findAll()).thenReturn(new ArrayList<>());

        List<ReportEntity> result = reportService.getReports();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void getReports_debeRetornarListaConUnReporte() {
        when(reportRepository.findAll()).thenReturn(new ArrayList<>(List.of(baseReport)));

        List<ReportEntity> result = reportService.getReports();

        assertEquals(1, result.size());
        assertEquals(baseReport, result.get(0));
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void getReports_debeRetornarListaConMultiplesReportes() {
        ReportEntity report2 = new ReportEntity(2L, "2024-01-01",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        ReportEntity report3 = new ReportEntity(3L, "2024-01-02",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        when(reportRepository.findAll())
                .thenReturn(new ArrayList<>(Arrays.asList(baseReport, report2, report3)));

        List<ReportEntity> result = reportService.getReports();

        assertEquals(3, result.size());
        assertTrue(result.contains(baseReport));
        assertTrue(result.contains(report2));
        assertTrue(result.contains(report3));
        verify(reportRepository, times(1)).findAll();
    }

    // ============================
    // getReportById(Long id)
    // ============================

    @Test
    void getReportById_debeRetornarReporteExistente() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(baseReport));

        ReportEntity result = reportService.getReportById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reportRepository, times(1)).findById(1L);
    }

    @Test
    void getReportById_debeLanzarExcepcionCuandoNoExiste() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reportService.getReportById(999L));
        verify(reportRepository, times(1)).findById(999L);
    }

    @Test
    void getReportById_debeRetornarReporteCorrectoParaIdsDiferentes() {
        ReportEntity report2 = new ReportEntity(2L, "2024-01-01",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        when(reportRepository.findById(1L)).thenReturn(Optional.of(baseReport));
        when(reportRepository.findById(2L)).thenReturn(Optional.of(report2));

        ReportEntity result1 = reportService.getReportById(1L);
        ReportEntity result2 = reportService.getReportById(2L);

        assertEquals(1L, result1.getId());
        assertEquals(2L, result2.getId());
        verify(reportRepository, times(1)).findById(1L);
        verify(reportRepository, times(1)).findById(2L);
    }

    // ============================
    // saveReport(ReportEntity report)
    // ============================

    @Test
    void saveReport_debePoblarCamposYGuardar() {
        // datos simulados
        var activeLoans = new ArrayList<>(List.of(new edu.mtisw.payrollbackend.entities.LoanData()));
        var delayedLoans = new ArrayList<>(List.of(new edu.mtisw.payrollbackend.entities.LoanData()));
        var delayedClients = new ArrayList<>(List.of("Juan", "Ana"));
        var topTools = new ArrayList<>(List.of("Martillo", "Taladro"));

        when(loanService.getActiveDelayedLoansData(false)).thenReturn(activeLoans);
        when(loanService.getActiveDelayedLoansData(true)).thenReturn(delayedLoans);
        when(clientService.getClientsWithDelayedLoans()).thenReturn(delayedClients);
        when(toolService.getTop10Tools()).thenReturn(topTools);
        when(reportRepository.save(any(ReportEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportEntity toSave = new ReportEntity();
        toSave.setCreationDate("2024-10-10");

        ReportEntity result = reportService.saveReport(toSave);

        assertEquals(activeLoans, result.getActiveLoans());
        assertEquals(delayedLoans, result.getDelayedLoans());
        assertEquals(delayedClients, result.getClientsWithDelayedLoans());
        assertEquals(topTools, result.getTopTools());
        verify(reportRepository, times(1)).save(result);
    }

    @Test
    void saveReport_debeManejarListasVacias() {
        when(loanService.getActiveDelayedLoansData(false)).thenReturn(new ArrayList<>());
        when(loanService.getActiveDelayedLoansData(true)).thenReturn(new ArrayList<>());
        when(clientService.getClientsWithDelayedLoans()).thenReturn(new ArrayList<>());
        when(toolService.getTop10Tools()).thenReturn(new ArrayList<>());
        when(reportRepository.save(any(ReportEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportEntity result = reportService.saveReport(new ReportEntity());

        assertNotNull(result.getActiveLoans());
        assertNotNull(result.getDelayedLoans());
        assertNotNull(result.getClientsWithDelayedLoans());
        assertNotNull(result.getTopTools());
        assertTrue(result.getActiveLoans().isEmpty());
        assertTrue(result.getDelayedLoans().isEmpty());
        assertTrue(result.getClientsWithDelayedLoans().isEmpty());
        assertTrue(result.getTopTools().isEmpty());
        verify(reportRepository, times(1)).save(result);
    }

    @Test
    void saveReport_debeSobrescribirDatosPreviosDelReporte() {
        // reporte con datos iniciales
        ReportEntity report = new ReportEntity();
        report.setCreationDate("2024-10-10");
        report.setActiveLoans(new ArrayList<>(List.of(new edu.mtisw.payrollbackend.entities.LoanData())));
        report.setDelayedLoans(new ArrayList<>(List.of(new edu.mtisw.payrollbackend.entities.LoanData())));
        report.setClientsWithDelayedLoans(new ArrayList<>(List.of("Viejo Cliente")));
        report.setTopTools(new ArrayList<>(List.of("Herramienta vieja")));

        // nuevos datos
        var newActive = new ArrayList<>(List.of(new edu.mtisw.payrollbackend.entities.LoanData()));
        var newDelayed = new ArrayList<>(List.of(new edu.mtisw.payrollbackend.entities.LoanData()));
        var newClients = new ArrayList<>(List.of("Nuevo Cliente"));
        var newTools = new ArrayList<>(List.of("Nueva herramienta"));

        when(loanService.getActiveDelayedLoansData(false)).thenReturn(newActive);
        when(loanService.getActiveDelayedLoansData(true)).thenReturn(newDelayed);
        when(clientService.getClientsWithDelayedLoans()).thenReturn(newClients);
        when(toolService.getTop10Tools()).thenReturn(newTools);
        when(reportRepository.save(any(ReportEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportEntity result = reportService.saveReport(report);

        assertEquals(newActive, result.getActiveLoans());
        assertEquals(newDelayed, result.getDelayedLoans());
        assertEquals(newClients, result.getClientsWithDelayedLoans());
        assertEquals(newTools, result.getTopTools());
    }

    // ============================
    // deleteReport(Long id)
    // ============================

    @Test
    void deleteReport_debeRetornarTrueCuandoEliminacionEsExitosa() throws Exception {
        doNothing().when(reportRepository).deleteById(1L);

        boolean result = reportService.deleteReport(1L);

        assertTrue(result);
        verify(reportRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReport_debeLanzarExceptionCuandoRepositorioFalla() {
        doThrow(new RuntimeException("error"))
                .when(reportRepository).deleteById(1L);

        Exception exception = assertThrows(Exception.class, () -> reportService.deleteReport(1L));

        assertEquals("error", exception.getMessage());
        verify(reportRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReport_debeInvocarDeleteByIdConIdCorrecto() throws Exception {
        Long id = 5L;
        doNothing().when(reportRepository).deleteById(id);

        boolean result = reportService.deleteReport(id);

        assertTrue(result);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(reportRepository).deleteById(captor.capture());
        assertEquals(id, captor.getValue());
    }

    // ============================
    // getReportsByDateRange(String startDate, String endDate)
    // ============================

    @Test
    void getReportsByDateRange_debeUsarFechasEspecificasCuandoNoSonVacias() {
        String start = "2024-01-01";
        String end = "2024-12-31";
        List<ReportEntity> expected = List.of(baseReport);

        when(reportRepository.findByCreationDateBetween(start, end)).thenReturn(expected);

        List<ReportEntity> result = reportService.getReportsByDateRange(start, end);

        assertEquals(expected, result);
        verify(reportRepository, times(1)).findByCreationDateBetween(start, end);
    }

    @Test
    void getReportsByDateRange_debeUsarMinComoInicioCuandoStartEsVacio() {
        String start = "";
        String end = "2024-12-31";

        String expectedStart = LocalDate.MIN.toString();
        List<ReportEntity> expected = List.of(baseReport);

        when(reportRepository.findByCreationDateBetween(expectedStart, end)).thenReturn(expected);

        List<ReportEntity> result = reportService.getReportsByDateRange(start, end);

        assertEquals(expected, result);
        verify(reportRepository, times(1)).findByCreationDateBetween(expectedStart, end);
    }

    @Test
    void getReportsByDateRange_debeUsarMaxComoFinCuandoEndEsVacio() {
        String start = "2024-01-01";
        String end = "";

        String expectedEnd = LocalDate.MAX.toString();
        List<ReportEntity> expected = List.of(baseReport);

        when(reportRepository.findByCreationDateBetween(start, expectedEnd)).thenReturn(expected);

        List<ReportEntity> result = reportService.getReportsByDateRange(start, end);

        assertEquals(expected, result);
        verify(reportRepository, times(1)).findByCreationDateBetween(start, expectedEnd);
    }
}
