package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.services.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test listLoans con 3 ejemplos: Vacío, Un Préstamo, Múltiples Préstamos")
    void listLoansTest() throws Exception {
        // Ejemplo 1: Lista vacía
        when(loanService.getLoans()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/loans/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Ejemplo 2: Lista con un préstamo activo
        LoanEntity loan1 = createLoan(1L, "Juan", "Martillo", true);
        when(loanService.getLoans()).thenReturn(List.of(loan1));

        mockMvc.perform(get("/api/v1/loans/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientName", is("Juan")));

        // Ejemplo 3: Lista con múltiples préstamos (uno activo, uno finalizado)
        LoanEntity loan2 = createLoan(2L, "Pedro", "Taladro", false); // Finalizado
        when(loanService.getLoans()).thenReturn(Arrays.asList(loan1, loan2));

        mockMvc.perform(get("/api/v1/loans/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].active", is(false)));
    }

    @Test
    @DisplayName("Test getLoan con 3 ejemplos: Préstamo Normal, Préstamo Atrasado, Préstamo Dañado")
    void getLoanTest() throws Exception {
        // Ejemplo 1: Préstamo normal ID 1
        LoanEntity loan1 = createLoan(1L, "Ana", "Sierra", true);
        when(loanService.getLoanById(1L)).thenReturn(loan1);

        mockMvc.perform(get("/api/v1/loans/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.toolName", is("Sierra")));

        // Ejemplo 2: Préstamo atrasado ID 2
        LoanEntity loan2 = createLoan(2L, "Luis", "Lijadora", true);
        loan2.setDelayed(true);
        loan2.setDelayFine(5000L);
        when(loanService.getLoanById(2L)).thenReturn(loan2);

        mockMvc.perform(get("/api/v1/loans/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delayed", is(true)))
                .andExpect(jsonPath("$.delayFine", is(5000)));

        // Ejemplo 3: Préstamo con herramienta dañada ID 3
        LoanEntity loan3 = createLoan(3L, "Maria", "Esmeril", false);
        loan3.setToolGotDamaged(true);
        when(loanService.getLoanById(3L)).thenReturn(loan3);

        mockMvc.perform(get("/api/v1/loans/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolGotDamaged", is(true)));
    }

    @Test
    @DisplayName("Test saveLoan con 3 ejemplos: Nuevo Préstamo A, Nuevo Préstamo B, Nuevo Préstamo C")
    void saveLoanTest() throws Exception {
        // Ejemplo 1: Guardar Préstamo básico
        LoanEntity inputA = createLoan(null, "Nuevo A", "Herramienta A", true);
        LoanEntity savedA = createLoan(10L, "Nuevo A", "Herramienta A", true);
        when(loanService.saveLoan(any(LoanEntity.class))).thenReturn(savedA);

        mockMvc.perform(post("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));

        // Ejemplo 2: Guardar Préstamo para otro cliente
        LoanEntity inputB = createLoan(null, "Cliente B", "Herramienta B", true);
        LoanEntity savedB = createLoan(11L, "Cliente B", "Herramienta B", true);
        when(loanService.saveLoan(any(LoanEntity.class))).thenReturn(savedB);

        mockMvc.perform(post("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName", is("Cliente B")));

        // Ejemplo 3: Guardar Préstamo con fecha específica de inicio
        LoanEntity inputC = createLoan(null, "Cliente C", "Herramienta C", true);
        inputC.setDateStart("2023-12-01");
        LoanEntity savedC = createLoan(12L, "Cliente C", "Herramienta C", true);
        savedC.setDateStart("2023-12-01");
        when(loanService.saveLoan(any(LoanEntity.class))).thenReturn(savedC);

        mockMvc.perform(post("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputC)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateStart", is("2023-12-01")));
    }

    @Test
    @DisplayName("Test updateLoan con 3 ejemplos: Finalizar Préstamo, Marcar Atraso, Marcar Daño")
    void updateLoanTest() throws Exception {
        // Ejemplo 1: Finalizar préstamo (setActive false)
        LoanEntity update1 = createLoan(1L, "Juan", "Martillo", false);
        update1.setDateReturn("2023-11-30");
        when(loanService.updateLoan(any(LoanEntity.class))).thenReturn(update1);

        mockMvc.perform(put("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)))
                .andExpect(jsonPath("$.dateReturn", is("2023-11-30")));

        // Ejemplo 2: Marcar como atrasado
        LoanEntity update2 = createLoan(2L, "Pedro", "Taladro", true);
        update2.setDelayed(true);
        when(loanService.updateLoan(any(LoanEntity.class))).thenReturn(update2);

        mockMvc.perform(put("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delayed", is(true)));

        // Ejemplo 3: Marcar daño en herramienta
        LoanEntity update3 = createLoan(3L, "Maria", "Sierra", false);
        update3.setToolGotDamaged(true);
        when(loanService.updateLoan(any(LoanEntity.class))).thenReturn(update3);

        mockMvc.perform(put("/api/v1/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolGotDamaged", is(true)));
    }

    @Test
    @DisplayName("Test deleteLoanById con 3 ejemplos: Borrar ID 1, Borrar ID 2, Borrar Inexistente")
    void deleteLoanByIdTest() throws Exception {
        // Ejemplo 1: Borrar ID 1 exitosamente
        when(loanService.deleteLoan(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/loans/{id}", 1L))
                .andExpect(status().isNoContent());

        // Ejemplo 2: Borrar ID 2 exitosamente
        when(loanService.deleteLoan(2L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/loans/{id}", 2L))
                .andExpect(status().isNoContent());

        // Ejemplo 3: Borrar ID 99 (no existe, servicio retorna false, pero controller responde NoContent)
        when(loanService.deleteLoan(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/loans/{id}", 99L))
                .andExpect(status().isNoContent());
    }

    // Helper method
    private LoanEntity createLoan(Long id, String clientName, String toolName, boolean isActive) {
        LoanEntity loan = new LoanEntity();
        loan.setId(id);
        loan.setClientId(100L); // Dummy ID
        loan.setClientName(clientName);
        loan.setToolId(200L); // Dummy ID
        loan.setToolName(toolName);
        loan.setDateStart("2023-01-01");
        loan.setActive(isActive);
        loan.setDelayed(false);
        loan.setToolGotDamaged(false);
        loan.setTotalTariff(0L);
        return loan;
    }
}
