package edu.mtisw.payrollbackend.controllers;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.services.KardexRegisterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KardexRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
public class KardexRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KardexRegisterService kardexRegisterService;

    @Autowired
    private ObjectMapper objectMapper;

    private KardexRegisterEntity buildKardex(Long id, String movement, Long toolId, String toolName,
                                             Long clientId, String clientName, int typeRelated, Long loanId,
                                             LocalDate date) {
        return new KardexRegisterEntity(
                id,
                movement,
                typeRelated,
                loanId,
                date,
                clientId,
                clientName,
                toolId,
                toolName
        );
    }

    // ----------------------------------------------------------------------
    // listKardexRegisters()  -->  GET /api/v1/kardexRegisters/
    // ----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/kardexRegisters/ - listKardexRegisters")
    class ListKardexRegistersTests {

        @Test
        @DisplayName("Ejemplo 1: lista vacía")
        void listKardexRegisters_emptyList() throws Exception {
            // Arrange
            given(kardexRegisterService.getKardexRegisters())
                    .willReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/v1/kardexRegisters/")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Ejemplo 2: lista con un sólo registro")
        void listKardexRegisters_singleElement() throws Exception {
            // Arrange
            KardexRegisterEntity k1 = buildKardex(
                    1L, "ENTRADA", 10L, "Martillo",
                    100L, "Juan Pérez", 1, null,
                    LocalDate.of(2024, 1, 10)
            );
            given(kardexRegisterService.getKardexRegisters())
                    .willReturn(List.of(k1));

            // Act & Assert
            mockMvc.perform(get("/api/v1/kardexRegisters/")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].movement", is("ENTRADA")))
                    .andExpect(jsonPath("$[0].toolName", is("Martillo")))
                    .andExpect(jsonPath("$[0].clientName", is("Juan Pérez")));
        }

        @Test
        @DisplayName("Ejemplo 3: lista con múltiples registros")
        void listKardexRegisters_multipleElements() throws Exception {
            // Arrange
            KardexRegisterEntity k1 = buildKardex(
                    1L, "ENTRADA", 10L, "Martillo",
                    100L, "Juan Pérez", 1, null,
                    LocalDate.of(2024, 1, 10)
            );
            KardexRegisterEntity k2 = buildKardex(
                    2L, "SALIDA", 11L, "Taladro",
                    101L, "María López", 2, 200L,
                    LocalDate.of(2024, 1, 11)
            );
            given(kardexRegisterService.getKardexRegisters())
                    .willReturn(Arrays.asList(k1, k2));

            // Act & Assert
            mockMvc.perform(get("/api/v1/kardexRegisters/")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[1].id", is(2)));
        }
    }

    // ----------------------------------------------------------------------
    // getKardexRegisterById(Long id)  -->  GET /api/v1/kardexRegisters/{id}
    // ----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/kardexRegisters/{id} - getKardexRegisterById")
    class GetKardexByIdTests {

        @Test
        @DisplayName("Ejemplo 1: ID 1 con registro existente")
        void getKardexById_1() throws Exception {
            KardexRegisterEntity k = buildKardex(
                    1L, "ENTRADA", 10L, "Martillo",
                    100L, "Juan Pérez", 1, null,
                    LocalDate.of(2024, 1, 10)
            );
            given(kardexRegisterService.getKardexRegisterById(1L)).willReturn(k);

            mockMvc.perform(get("/api/v1/kardexRegisters/{id}", 1L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.movement", is("ENTRADA")))
                    .andExpect(jsonPath("$.toolName", is("Martillo")));
        }

        @Test
        @DisplayName("Ejemplo 2: ID 5 con otro registro")
        void getKardexById_5() throws Exception {
            KardexRegisterEntity k = buildKardex(
                    5L, "SALIDA", 11L, "Taladro",
                    101L, "María López", 2, 200L,
                    LocalDate.of(2024, 2, 5)
            );
            given(kardexRegisterService.getKardexRegisterById(5L)).willReturn(k);

            mockMvc.perform(get("/api/v1/kardexRegisters/{id}", 5L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.movement", is("SALIDA")))
                    .andExpect(jsonPath("$.clientName", is("María López")));
        }

        @Test
        @DisplayName("Ejemplo 3: ID 99 con registro genérico")
        void getKardexById_99() throws Exception {
            KardexRegisterEntity k = buildKardex(
                    99L, "AJUSTE", 12L, "Llave Inglesa",
                    102L, "Carlos Díaz", 1, null,
                    LocalDate.of(2024, 3, 1)
            );
            given(kardexRegisterService.getKardexRegisterById(99L)).willReturn(k);

            mockMvc.perform(get("/api/v1/kardexRegisters/{id}", 99L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(99)))
                    .andExpect(jsonPath("$.movement", is("AJUSTE")))
                    .andExpect(jsonPath("$.toolName", is("Llave Inglesa")));
        }
    }

    // ----------------------------------------------------------------------
    // getKardexRegisterInDateRange(startDate, endDate)
    //    --> GET /api/v1/kardexRegisters/dateRange
    // ----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/kardexRegisters/dateRange - getKardexRegisterInDateRange")
    class GetKardexByDateRangeTests {

        @Test
        @DisplayName("Ejemplo 1: rango completo startDate y endDate")
        void getKardexInDateRange_fullRange() throws Exception {
            String start = "2024-01-01";
            String end = "2024-01-31";

            KardexRegisterEntity k1 = buildKardex(
                    1L, "ENTRADA", 10L, "Martillo",
                    100L, "Juan Pérez", 1, null,
                    LocalDate.of(2024, 1, 10)
            );
            KardexRegisterEntity k2 = buildKardex(
                    2L, "SALIDA", 11L, "Taladro",
                    101L, "María López", 2, 200L,
                    LocalDate.of(2024, 1, 20)
            );
            given(kardexRegisterService.getKardexRegisterInDateRange(start, end))
                    .willReturn(Arrays.asList(k1, k2));

            mockMvc.perform(get("/api/v1/kardexRegisters/dateRange")
                            .param("startDate", start)
                            .param("endDate", end)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[1].id", is(2)));
        }

        @Test
        @DisplayName("Ejemplo 2: sólo startDate, sin endDate")
        void getKardexInDateRange_onlyStartDate() throws Exception {
            String start = "2024-02-01";

            KardexRegisterEntity k1 = buildKardex(
                    3L, "ENTRADA", 12L, "Llave Inglesa",
                    102L, "Carlos Díaz", 1, null,
                    LocalDate.of(2024, 2, 5)
            );
            given(kardexRegisterService.getKardexRegisterInDateRange(start, null))
                    .willReturn(List.of(k1));

            mockMvc.perform(get("/api/v1/kardexRegisters/dateRange")
                            .param("startDate", start)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(3)))
                    .andExpect(jsonPath("$[0].movement", is("ENTRADA")));
        }

        @Test
        @DisplayName("Ejemplo 3: sin parámetros (ambos null)")
        void getKardexInDateRange_noParams() throws Exception {
            given(kardexRegisterService.getKardexRegisterInDateRange(null, null))
                    .willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/kardexRegisters/dateRange")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ----------------------------------------------------------------------
    // getKardexRegisterByTool(Long toolId)
    //    --> GET /api/v1/kardexRegisters/tools/{toolId}
    // ----------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/kardexRegisters/tools/{toolId} - getKardexRegisterByTool")
    class GetKardexByToolTests {

        @Test
        @DisplayName("Ejemplo 1: toolId = 10 con un registro")
        void getKardexByTool_10() throws Exception {
            Long toolId = 10L;
            KardexRegisterEntity k1 = buildKardex(
                    1L, "ENTRADA", toolId, "Martillo",
                    100L, "Juan Pérez", 1, null,
                    LocalDate.of(2024, 1, 10)
            );
            given(kardexRegisterService.getKardexRegisterByToolName(toolId))
                    .willReturn(List.of(k1));

            mockMvc.perform(get("/api/v1/kardexRegisters/tools/{toolId}", toolId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].toolId", is(10)))
                    .andExpect(jsonPath("$[0].toolName", is("Martillo")));
        }

        @Test
        @DisplayName("Ejemplo 2: toolId = 11 con múltiples registros")
        void getKardexByTool_11_multiple() throws Exception {
            Long toolId = 11L;
            KardexRegisterEntity k1 = buildKardex(
                    2L, "ENTRADA", toolId, "Taladro",
                    101L, "María López", 1, null,
                    LocalDate.of(2024, 1, 15)
            );
            KardexRegisterEntity k2 = buildKardex(
                    3L, "SALIDA", toolId, "Taladro",
                    103L, "Pedro Gómez", 2, 201L,
                    LocalDate.of(2024, 1, 18)
            );
            given(kardexRegisterService.getKardexRegisterByToolName(toolId))
                    .willReturn(Arrays.asList(k1, k2));

            mockMvc.perform(get("/api/v1/kardexRegisters/tools/{toolId}", toolId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].toolId", is(11)))
                    .andExpect(jsonPath("$[1].toolId", is(11)));
        }

        @Test
        @DisplayName("Ejemplo 3: toolId = 99 sin registros")
        void getKardexByTool_99_empty() throws Exception {
            Long toolId = 99L;
            given(kardexRegisterService.getKardexRegisterByToolName(toolId))
                    .willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/kardexRegisters/tools/{toolId}", toolId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ----------------------------------------------------------------------
    // deleteKardexRegister(Long id)  -->  DELETE /api/v1/kardexRegisters/{id}
    // ----------------------------------------------------------------------
    @Nested
    @DisplayName("DELETE /api/v1/kardexRegisters/{id} - deleteKardexRegister")
    class DeleteKardexTests {

        @Test
        @DisplayName("Ejemplo 1: eliminar ID 1")
        void deleteKardex_1() throws Exception {
            doReturn(true).when(kardexRegisterService).deleteKardexRegister(1L);

            mockMvc.perform(delete("/api/v1/kardexRegisters/{id}", 1L))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Ejemplo 2: eliminar ID 5")
        void deleteKardex_5() throws Exception {
            doReturn(true).when(kardexRegisterService).deleteKardexRegister(5L);

            mockMvc.perform(delete("/api/v1/kardexRegisters/{id}", 5L))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Ejemplo 3: eliminar ID 99")
        void deleteKardex_99() throws Exception {
            doReturn(true).when(kardexRegisterService).deleteKardexRegister(99L);

            mockMvc.perform(delete("/api/v1/kardexRegisters/{id}", 99L))
                    .andExpect(status().isNoContent());
        }
    }
}
