package edu.mtisw.payrollbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.services.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TariffController.class)
public class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffService tariffService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        Mockito.reset(tariffService);
    }

    @Test
    void testGetTariff() throws Exception {
        // Ejemplo 1: Retornar una tarifa válida
        TariffEntity tariff = new TariffEntity(1L, 1000L, 2000L);
        when(tariffService.getTariff()).thenReturn(tariff);

        mockMvc.perform(get("/tariff")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dailyTariff").value(1000L))
                .andExpect(jsonPath("$.delayTariff").value(2000L));

        // Ejemplo 2: Retornar una tarifa con valores diferentes
        TariffEntity tariff2 = new TariffEntity(1L, 1500L, 3000L);
        when(tariffService.getTariff()).thenReturn(tariff2);

        mockMvc.perform(get("/tariff")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dailyTariff").value(1500L))
                .andExpect(jsonPath("$.delayTariff").value(3000L));

        // Ejemplo 3: Lanza un error al no encontrar la tarifa
        when(tariffService.getTariff()).thenThrow(new RuntimeException("Tariff not found"));

        mockMvc.perform(get("/tariff")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Tariff not found"));
    }

    @Test
    void testUpdateTariff() throws Exception {
        // Ejemplo 1: Actualizar con valores válidos
        TariffEntity updatedTariff = new TariffEntity(1L, 1200L, 2200L);
        when(tariffService.updateTariff(any(TariffEntity.class))).thenReturn(updatedTariff);

        mockMvc.perform(put("/tariff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTariff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dailyTariff").value(1200L))
                .andExpect(jsonPath("$.delayTariff").value(2200L));

        // Ejemplo 2: Actualizar con valores extremos
        TariffEntity extremeTariff = new TariffEntity(1L, 0L, Long.MAX_VALUE);
        when(tariffService.updateTariff(any(TariffEntity.class))).thenReturn(extremeTariff);

        mockMvc.perform(put("/tariff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(extremeTariff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTariff").value(0L))
                .andExpect(jsonPath("$.delayTariff").value(Long.MAX_VALUE));

        // Ejemplo 3: Intentar actualizar con datos inválidos (sin ID)
        TariffEntity invalidTariff = new TariffEntity(null, 1000L, 2000L);

        mockMvc.perform(put("/tariff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTariff)))
                .andExpect(status().isBadRequest()); // Asumiendo que el controlador valida este caso
    }

    @Test
    void testCheckTariffOnStartup() throws Exception {
        // Ejemplo 1: Se inicializan correctamente tarifas cuando no hay ninguna
        mockMvc.perform(post("/tariff/check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verifica que el servicio sea llamado
        Mockito.verify(tariffService, Mockito.times(1)).checkTariff();

        // Ejemplo 2: Lógica para revalidar cuando ya existen tarifas (sin impacto real visible aquí)
        mockMvc.perform(post("/tariff/check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(tariffService, Mockito.times(2)).checkTariff();

        // Ejemplo 3: Error interno al procesar (forzamos un fallo en el servicio)
        Mockito.doThrow(new RuntimeException("Internal Error")).when(tariffService).checkTariff();

        mockMvc.perform(post("/tariff/check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Internal Error"));
    }
}
