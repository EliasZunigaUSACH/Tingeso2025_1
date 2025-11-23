package edu.mtisw.payrollbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.services.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test listReports con 3 ejemplos: Vacío, Un Reporte, Múltiples Reportes")
    void listReportsTest() throws Exception {
        // Ejemplo 1: Lista vacía
        when(reportService.getReports()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reports/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Ejemplo 2: Lista con un reporte
        ReportEntity report1 = createReport(1L, "2023-10-01");
        when(reportService.getReports()).thenReturn(List.of(report1));

        mockMvc.perform(get("/api/v1/reports/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].creationDate", is("2023-10-01")));

        // Ejemplo 3: Lista con múltiples reportes
        ReportEntity report2 = createReport(2L, "2023-10-15");
        when(reportService.getReports()).thenReturn(Arrays.asList(report1, report2));

        mockMvc.perform(get("/api/v1/reports/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].creationDate", is("2023-10-15")));
    }

    @Test
    @DisplayName("Test getReportById con 3 ejemplos: ID 1, ID 5, ID 100")
    void getReportByIdTest() throws Exception {
        // Ejemplo 1: Reporte ID 1
        ReportEntity report1 = createReport(1L, "2023-01-01");
        when(reportService.getReportById(1L)).thenReturn(report1);

        mockMvc.perform(get("/api/v1/reports/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.creationDate", is("2023-01-01")));

        // Ejemplo 2: Reporte ID 5
        ReportEntity report5 = createReport(5L, "2023-05-05");
        when(reportService.getReportById(5L)).thenReturn(report5);

        mockMvc.perform(get("/api/v1/reports/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));

        // Ejemplo 3: Reporte ID 100 (con datos simulados de herramientas)
        ReportEntity report100 = createReport(100L, "2023-12-31");
        report100.setTopTools(List.of("Martillo", "Taladro"));
        when(reportService.getReportById(100L)).thenReturn(report100);

        mockMvc.perform(get("/api/v1/reports/{id}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topTools", hasSize(2)));
    }

    @Test
    @DisplayName("Test saveReport con 3 ejemplos: Reporte Simple, Reporte con Morosos, Reporte con Top Tools")
    void saveReportTest() throws Exception {
        // Ejemplo 1: Reporte Simple
        ReportEntity inputA = createReport(null, "2023-11-01");
        ReportEntity savedA = createReport(10L, "2023-11-01");
        when(reportService.saveReport(any(ReportEntity.class))).thenReturn(savedA);

        mockMvc.perform(post("/api/v1/reports/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));

        // Ejemplo 2: Reporte con clientes morosos
        ReportEntity inputB = createReport(null, "2023-11-02");
        inputB.setClientsWithDelayedLoans(List.of("Juan Perez"));
        ReportEntity savedB = createReport(11L, "2023-11-02");
        savedB.setClientsWithDelayedLoans(List.of("Juan Perez"));
        when(reportService.saveReport(any(ReportEntity.class))).thenReturn(savedB);

        mockMvc.perform(post("/api/v1/reports/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientsWithDelayedLoans[0]", is("Juan Perez")));

        // Ejemplo 3: Reporte con Top Tools
        ReportEntity inputC = createReport(null, "2023-11-03");
        inputC.setTopTools(List.of("Sierra"));
        ReportEntity savedC = createReport(12L, "2023-11-03");
        savedC.setTopTools(List.of("Sierra"));
        when(reportService.saveReport(any(ReportEntity.class))).thenReturn(savedC);

        mockMvc.perform(post("/api/v1/reports/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputC)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topTools[0]", is("Sierra")));
    }

    @Test
    @DisplayName("Test deleteReportById con 3 ejemplos: Éxito ID 1, Éxito ID 2, Fallo ID 3")
    void deleteReportByIdTest() throws Exception {
        // Ejemplo 1: Borrar ID 1 (devuelve true el servicio)
        when(reportService.deleteReport(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/reports/{id}", 1L))
                .andExpect(status().isNoContent());

        // Ejemplo 2: Borrar ID 2 (devuelve true el servicio)
        when(reportService.deleteReport(2L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/reports/{id}", 2L))
                .andExpect(status().isNoContent());

        // Ejemplo 3: Borrar ID 3 (devuelve false el servicio, controller igual retorna NoContent)
        when(reportService.deleteReport(3L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/reports/{id}", 3L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test getReportsByDateRange con 3 ejemplos: Rango Invierno, Rango Verano, Sin Resultados")
    void getReportsByDateRangeTest() throws Exception {
        // Ejemplo 1: Rango Invierno con resultados
        ReportEntity r1 = createReport(1L, "2023-06-01");
        when(reportService.getReportsByDateRange("2023-06-01", "2023-06-30"))
                .thenReturn(List.of(r1));

        mockMvc.perform(get("/api/v1/reports/dateRange")
                        .param("startDate", "2023-06-01")
                        .param("endDate", "2023-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Ejemplo 2: Rango Verano con múltiples resultados
        ReportEntity r2 = createReport(2L, "2023-01-10");
        ReportEntity r3 = createReport(3L, "2023-01-20");
        when(reportService.getReportsByDateRange("2023-01-01", "2023-02-01"))
                .thenReturn(Arrays.asList(r2, r3));

        mockMvc.perform(get("/api/v1/reports/dateRange")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-02-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Ejemplo 3: Rango sin resultados
        when(reportService.getReportsByDateRange("2025-01-01", "2025-01-31"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reports/dateRange")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Helper method
    private ReportEntity createReport(Long id, String date) {
        ReportEntity report = new ReportEntity();
        report.setId(id);
        report.setCreationDate(date);
        report.setActiveLoans(new ArrayList<>());
        report.setDelayedLoans(new ArrayList<>());
        report.setClientsWithDelayedLoans(new ArrayList<>());
        report.setTopTools(new ArrayList<>());
        return report;
    }
}
