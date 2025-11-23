package edu.mtisw.payrollbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.services.TariffService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TariffController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TariffService tariffService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test getTariff con 3 ejemplos: Tarifa Estándar, Tarifa Alta, Tarifa Baja")
    void getTariffTest() throws Exception {
        // Ejemplo 1: Tarifa Estándar
        TariffEntity standardTariff = new TariffEntity(1L, 500L, 750L);
        when(tariffService.getTariff()).thenReturn(standardTariff);

        mockMvc.perform(get("/api/v1/tariff/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTariff", is(500)))
                .andExpect(jsonPath("$.delayTariff", is(750)));

        // Ejemplo 2: Tarifa Alta (Inflación)
        TariffEntity highTariff = new TariffEntity(1L, 1500L, 2500L);
        when(tariffService.getTariff()).thenReturn(highTariff);

        mockMvc.perform(get("/api/v1/tariff/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTariff", is(1500)))
                .andExpect(jsonPath("$.delayTariff", is(2500)));

        // Ejemplo 3: Tarifa Baja (Promoción)
        TariffEntity lowTariff = new TariffEntity(1L, 100L, 200L);
        when(tariffService.getTariff()).thenReturn(lowTariff);

        mockMvc.perform(get("/api/v1/tariff/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTariff", is(100)))
                .andExpect(jsonPath("$.delayTariff", is(200)));
    }

    @Test
    @DisplayName("Test updateTariff con 3 ejemplos: Modificación Parcial, Modificación Completa, Valores Cero")
    void updateTariffTest() throws Exception {
        // Ejemplo 1: Modificación Parcial (Solo sube tarifa diaria)
        TariffEntity input1 = new TariffEntity(1L, 600L, 750L);
        when(tariffService.updateTariff(any(TariffEntity.class))).thenReturn(input1);

        mockMvc.perform(put("/api/v1/tariff/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTariff", is(600)));

        // Ejemplo 2: Modificación Completa (Cambian ambos valores drásticamente)
        TariffEntity input2 = new TariffEntity(1L, 5000L, 10000L);
        when(tariffService.updateTariff(any(TariffEntity.class))).thenReturn(input2);

        mockMvc.perform(put("/api/v1/tariff/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTariff", is(5000)))
                .andExpect(jsonPath("$.delayTariff", is(10000)));

        // Ejemplo 3: Valores en Cero (Gratuidad)
        TariffEntity input3 = new TariffEntity(1L, 0L, 0L);
        when(tariffService.updateTariff(any(TariffEntity.class))).thenReturn(input3);

        mockMvc.perform(put("/api/v1/tariff/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTariff", is(0)))
                .andExpect(jsonPath("$.delayTariff", is(0)));
    }
}
