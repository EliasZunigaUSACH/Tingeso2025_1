package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.services.ToolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mtisw.payrollbackend.entities.LoanData;
import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.services.ToolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ToolController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad para simplificar tests
public class ToolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ToolService toolService;

    @Autowired
    private ObjectMapper objectMapper;

    private ToolEntity tool1;
    private ToolEntity tool2;
    private ToolEntity tool3;

    @BeforeEach
    void setUp() {
        List<LoanData> historyEmpty = new ArrayList<>();

        tool1 = new ToolEntity(
                1L,
                "Taladro",
                "Electricas",
                3,
                historyEmpty,
                10000L
        );

        tool2 = new ToolEntity(
                2L,
                "Martillo",
                "Manuales",
                2,
                historyEmpty,
                5000L
        );

        tool3 = new ToolEntity(
                3L,
                "Sierra",
                "Electricas",
                1,
                historyEmpty,
                15000L
        );
    }

    // -------------------------------------------------------------------------
    // listTools -> GET /api/v1/tools/
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/tools/ - listTools")
    class ListToolsTests {

        @Test
        void listTools_debeRetornarListaVacia() throws Exception {
            given(toolService.getAllTools()).willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/tools/"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void listTools_debeRetornarUnSoloElemento() throws Exception {
            given(toolService.getAllTools()).willReturn(List.of(tool1));

            mockMvc.perform(get("/api/v1/tools/"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(tool1.getId().intValue())))
                    .andExpect(jsonPath("$[0].name", is(tool1.getName())))
                    .andExpect(jsonPath("$[0].category", is(tool1.getCategory())))
                    .andExpect(jsonPath("$[0].status", is(tool1.getStatus())))
                    .andExpect(jsonPath("$[0].price", is(tool1.getPrice().intValue())));
        }

        @Test
        void listTools_debeRetornarMultiplesElementos() throws Exception {
            given(toolService.getAllTools()).willReturn(Arrays.asList(tool1, tool2, tool3));

            mockMvc.perform(get("/api/v1/tools/"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].name", is(tool1.getName())))
                    .andExpect(jsonPath("$[1].name", is(tool2.getName())))
                    .andExpect(jsonPath("$[2].name", is(tool3.getName())));
        }
    }

    // -------------------------------------------------------------------------
    // getTool -> GET /api/v1/tools/{id}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/tools/{id} - getTool")
    class GetToolTests {

        @Test
        void getTool_debeRetornarToolConId1() throws Exception {
            given(toolService.getToolById(1L)).willReturn(tool1);

            mockMvc.perform(get("/api/v1/tools/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(tool1.getId().intValue())))
                    .andExpect(jsonPath("$.name", is(tool1.getName())));
        }

        @Test
        void getTool_debeRetornarToolConId2() throws Exception {
            given(toolService.getToolById(2L)).willReturn(tool2);

            mockMvc.perform(get("/api/v1/tools/{id}", 2L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(tool2.getId().intValue())))
                    .andExpect(jsonPath("$.category", is(tool2.getCategory())));
        }

        @Test
        void getTool_debeRetornarToolConId3() throws Exception {
            given(toolService.getToolById(3L)).willReturn(tool3);

            mockMvc.perform(get("/api/v1/tools/{id}", 3L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(tool3.getId().intValue())))
                    .andExpect(jsonPath("$.status", is(tool3.getStatus())));
        }
    }

    // -------------------------------------------------------------------------
    // saveTool -> POST /api/v1/tools/
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("POST /api/v1/tools/ - saveTool")
    class SaveToolTests {

        @Test
        void saveTool_debeCrearTaladro() throws Exception {
            ToolEntity input = new ToolEntity(
                    null,
                    "Taladro Nuevo",
                    "Electricas",
                    3,
                    new ArrayList<>(),
                    12000L
            );
            ToolEntity saved = new ToolEntity(
                    10L,
                    input.getName(),
                    input.getCategory(),
                    input.getStatus(),
                    input.getHistory(),
                    input.getPrice()
            );

            given(toolService.saveTool(any(ToolEntity.class))).willReturn(saved);

            mockMvc.perform(post("/api/v1/tools/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                    .andExpect(jsonPath("$.name", is(saved.getName())));
        }

        @Test
        void saveTool_debeCrearMartillo() throws Exception {
            ToolEntity input = new ToolEntity(
                    null,
                    "Martillo Pro",
                    "Manuales",
                    3,
                    new ArrayList<>(),
                    7000L
            );
            ToolEntity saved = new ToolEntity(
                    11L,
                    input.getName(),
                    input.getCategory(),
                    input.getStatus(),
                    input.getHistory(),
                    input.getPrice()
            );

            given(toolService.saveTool(any(ToolEntity.class))).willReturn(saved);

            mockMvc.perform(post("/api/v1/tools/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                    .andExpect(jsonPath("$.category", is(saved.getCategory())));
        }

        @Test
        void saveTool_debeCrearSierra() throws Exception {
            ToolEntity input = new ToolEntity(
                    null,
                    "Sierra Circular",
                    "Electricas",
                    3,
                    new ArrayList<>(),
                    20000L
            );
            ToolEntity saved = new ToolEntity(
                    12L,
                    input.getName(),
                    input.getCategory(),
                    input.getStatus(),
                    input.getHistory(),
                    input.getPrice()
            );

            given(toolService.saveTool(any(ToolEntity.class))).willReturn(saved);

            mockMvc.perform(post("/api/v1/tools/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                    .andExpect(jsonPath("$.price", is(saved.getPrice().intValue())));
        }
    }

    // -------------------------------------------------------------------------
    // updateTool -> PUT /api/v1/tools/
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("PUT /api/v1/tools/ - updateTool")
    class UpdateToolTests {

        @Test
        void updateTool_debeActualizarNombreTaladro() throws Exception {
            ToolEntity toUpdate = new ToolEntity(
                    1L,
                    "Taladro Actualizado",
                    tool1.getCategory(),
                    tool1.getStatus(),
                    tool1.getHistory(),
                    tool1.getPrice()
            );

            given(toolService.updateTool(any(ToolEntity.class))).willReturn(toUpdate);

            mockMvc.perform(put("/api/v1/tools/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(toUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(toUpdate.getId().intValue())))
                    .andExpect(jsonPath("$.name", is("Taladro Actualizado")));
        }

        @Test
        void updateTool_debeCambiarCategoriaMartillo() throws Exception {
            ToolEntity toUpdate = new ToolEntity(
                    2L,
                    tool2.getName(),
                    "Herramientas de Golpe",
                    tool2.getStatus(),
                    tool2.getHistory(),
                    tool2.getPrice()
            );

            given(toolService.updateTool(any(ToolEntity.class))).willReturn(toUpdate);

            mockMvc.perform(put("/api/v1/tools/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(toUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(toUpdate.getId().intValue())))
                    .andExpect(jsonPath("$.category", is("Herramientas de Golpe")));
        }

        @Test
        void updateTool_debeCambiarPrecioSierra() throws Exception {
            ToolEntity toUpdate = new ToolEntity(
                    3L,
                    tool3.getName(),
                    tool3.getCategory(),
                    tool3.getStatus(),
                    tool3.getHistory(),
                    18000L
            );

            given(toolService.updateTool(any(ToolEntity.class))).willReturn(toUpdate);

            mockMvc.perform(put("/api/v1/tools/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(toUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(toUpdate.getId().intValue())))
                    .andExpect(jsonPath("$.price", is(18000)));
        }
    }

    // -------------------------------------------------------------------------
    // listToolsByStatus -> GET /api/v1/tools/status/{status}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/tools/status/{status} - listToolsByStatus")
    class ListToolsByStatusTests {

        @Test
        void listToolsByStatus_status3_debeRetornarDisponibles() throws Exception {
            given(toolService.getToolsByStatus(3)).willReturn(List.of(tool1));

            mockMvc.perform(get("/api/v1/tools/status/{status}", 3))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status", is(3)));
        }

        @Test
        void listToolsByStatus_status2_debeRetornarPrestadas() throws Exception {
            ToolEntity prestada = new ToolEntity(
                    4L,
                    "Compresor",
                    "Electricas",
                    2,
                    new ArrayList<>(),
                    30000L
            );
            given(toolService.getToolsByStatus(2)).willReturn(List.of(prestada));

            mockMvc.perform(get("/api/v1/tools/status/{status}", 2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status", is(2)))
                    .andExpect(jsonPath("$[0].name", is("Compresor")));
        }

        @Test
        void listToolsByStatus_status1_debeRetornarEnReparacionOVacio() throws Exception {
            given(toolService.getToolsByStatus(1)).willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/tools/status/{status}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // -------------------------------------------------------------------------
    // listToolsByCategory -> GET /api/v1/tools/category/{category}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /api/v1/tools/category/{category} - listToolsByCategory")
    class ListToolsByCategoryTests {

        @Test
        void listToolsByCategory_electricas_debeRetornarElectricas() throws Exception {
            given(toolService.getToolsByCategory("Electricas"))
                    .willReturn(List.of(tool1, tool3));

            mockMvc.perform(get("/api/v1/tools/category/{category}", "Electricas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].category", is("Electricas")))
                    .andExpect(jsonPath("$[1].category", is("Electricas")));
        }

        @Test
        void listToolsByCategory_manuales_debeRetornarManuales() throws Exception {
            given(toolService.getToolsByCategory("Manuales"))
                    .willReturn(List.of(tool2));

            mockMvc.perform(get("/api/v1/tools/category/{category}", "Manuales"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].category", is("Manuales")))
                    .andExpect(jsonPath("$[0].name", is(tool2.getName())));
        }

        @Test
        void listToolsByCategory_inexistente_debeRetornarListaVacia() throws Exception {
            given(toolService.getToolsByCategory("Inexistente"))
                    .willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/tools/category/{category}", "Inexistente"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // -------------------------------------------------------------------------
    // deleteToolById -> DELETE /api/v1/tools/{id}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("DELETE /api/v1/tools/{id} - deleteToolById")
    class DeleteToolTests {

        @Test
        void deleteToolById_debeRetornar204CuandoExiste() throws Exception {
            given(toolService.deleteTool(1L)).willReturn(true);

            mockMvc.perform(delete("/api/v1/tools/{id}", 1L))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteToolById_debeRetornar204AunqueServicioRetorneFalse() throws Exception {
            given(toolService.deleteTool(999L)).willReturn(false);

            mockMvc.perform(delete("/api/v1/tools/{id}", 999L))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteToolById_debeRetornar204ConOtroIdValido() throws Exception {
            given(toolService.deleteTool(2L)).willReturn(true);

            mockMvc.perform(delete("/api/v1/tools/{id}", 2L))
                    .andExpect(status().isNoContent());
        }
    }
}
