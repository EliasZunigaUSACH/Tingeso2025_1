package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.repositories.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TariffServiceTest {

    @InjectMocks
    private TariffService tariffService;

    @Mock
    private TariffRepository tariffRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTariff() {
        // Ejemplo 1: Retorna un `TariffEntity` válido
        TariffEntity tariff = new TariffEntity(1L, 1000L, 2000L);
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));

        TariffEntity result = tariffService.getTariff();
        assertEquals(1000L, result.getDailyTariff());
        assertEquals(2000L, result.getDelayTariff());
        verify(tariffRepository, times(1)).findById(1L);

        // Ejemplo 2: Retorna un `TariffEntity` con diferentes valores
        TariffEntity tariff2 = new TariffEntity(1L, 1500L, 2500L);
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff2));

        result = tariffService.getTariff();
        assertEquals(1500L, result.getDailyTariff());
        assertEquals(2500L, result.getDelayTariff());
        verify(tariffRepository, times(2)).findById(1L);

        // Ejemplo 3: Lanza excepción debido a que no encuentra el ID
        when(tariffRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            tariffService.getTariff();
        } catch (Exception e) {
            assertEquals("java.util.NoSuchElementException", e.getClass().getName());
        }
        verify(tariffRepository, times(3)).findById(1L);
    }

    @Test
    void testUpdateTariff() {
        // Ejemplo 1: Actualizar con valores válidos
        TariffEntity tariff = new TariffEntity(1L, 1100L, 2200L);
        when(tariffRepository.save(any(TariffEntity.class))).thenReturn(tariff); // Simula el comportamiento del repositorio

        TariffEntity result = tariffService.updateTariff(tariff); // Invoca el método del servicio
        assertEquals(1100L, result.getDailyTariff()); // Verifica que los valores sean los esperados
        assertEquals(2200L, result.getDelayTariff());
        verify(tariffRepository, times(1)).save(tariff); // Asegura que save solo se llame una vez con "tariff"

        // Ejemplo 2: Actualizar con diferentes valores
        TariffEntity tariff2 = new TariffEntity(1L, 1200L, 2400L);
        when(tariffRepository.save(any(TariffEntity.class))).thenReturn(tariff2); // Aquí se vuelve a configurar el mock

        result = tariffService.updateTariff(tariff2); // Invoca el método del servicio nuevamente
        assertEquals(1200L, result.getDailyTariff()); // Verifica nuevamente los valores
        assertEquals(2400L, result.getDelayTariff());
        verify(tariffRepository, times(1)).save(tariff2); // Cada verify es único por cada llamada configurada en "when"

        // Ejemplo 3: Actualizar con valores extremos
        TariffEntity tariff3 = new TariffEntity(1L, 0L, Long.MAX_VALUE);
        when(tariffRepository.save(any(TariffEntity.class))).thenReturn(tariff3); // Tercer configuración del mock

        result = tariffService.updateTariff(tariff3); // Tercer invocación
        assertEquals(0L, result.getDailyTariff()); // Validación con valores extremos
        assertEquals(Long.MAX_VALUE, result.getDelayTariff());
        verify(tariffRepository, times(1)).save(tariff3); // Verifica que save funciona exactamente una vez con "tariff3"
    }

    @Test
    void testCheckTariff() {
        // Ejemplo 1: No hay tarifas existentes, crea una nueva
        when(tariffRepository.count()).thenReturn(0L);
        when(tariffRepository.save(any(TariffEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        tariffService.checkTariff();

        verify(tariffRepository, times(1)).count();
        verify(tariffRepository, times(1)).save(any(TariffEntity.class));

        // Ejemplo 2: Ya existe una tarifa, no crea una nueva
        when(tariffRepository.count()).thenReturn(1L);

        tariffService.checkTariff();

        verify(tariffRepository, times(2)).count();
        verify(tariffRepository, times(1)).save(any(TariffEntity.class)); // No debería incrementarse

        // Ejemplo 3: Caso extremo, no realiza cambios porque ya tiene registros
        when(tariffRepository.count()).thenReturn(10L);

        tariffService.checkTariff();

        verify(tariffRepository, times(3)).count();
        verify(tariffRepository, times(1)).save(any(TariffEntity.class)); // No debería incrementarse
    }
}
