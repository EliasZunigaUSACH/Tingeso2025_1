package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.TariffEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TariffRepositoryTest {

    @Autowired
    private TariffRepository tariffRepository;

    @BeforeEach
    void setup() {
        // Limpia el repositorio antes de cada prueba
        tariffRepository.deleteAll();
    }

    @Test
    void testSaveTariff() {
        // Ejemplo 1: Guardar un registro válido
        TariffEntity tariff1 = new TariffEntity(1L, 1000L, 2000L);
        TariffEntity savedTariff1 = tariffRepository.save(tariff1);
        assertNotNull(savedTariff1);
        assertEquals(1000L, savedTariff1.getDailyTariff());
        assertEquals(2000L, savedTariff1.getDelayTariff());

        // Ejemplo 2: Guardar un registro con valores diferentes
        TariffEntity tariff2 = new TariffEntity(2L, 1500L, 2500L);
        TariffEntity savedTariff2 = tariffRepository.save(tariff2);
        assertNotNull(savedTariff2);
        assertEquals(1500L, savedTariff2.getDailyTariff());
        assertEquals(2500L, savedTariff2.getDelayTariff());

        // Ejemplo 3: Guardar un registro con valores extremos
        TariffEntity tariff3 = new TariffEntity(3L, 0L, Long.MAX_VALUE);
        TariffEntity savedTariff3 = tariffRepository.save(tariff3);
        assertNotNull(savedTariff3);
        assertEquals(0L, savedTariff3.getDailyTariff());
        assertEquals(Long.MAX_VALUE, savedTariff3.getDelayTariff());
    }

    @Test
    void testFindById() {
        // Ejemplo 1: Buscar un registro existente
        TariffEntity tariff = new TariffEntity(1L, 1000L, 2000L);
        tariffRepository.save(tariff);
        Optional<TariffEntity> foundTariff = tariffRepository.findById(1L);
        assertTrue(foundTariff.isPresent());
        assertEquals(1000L, foundTariff.get().getDailyTariff());
        assertEquals(2000L, foundTariff.get().getDelayTariff());

        // Ejemplo 2: Intentar buscar un ID inexistente
        Optional<TariffEntity> nonExistingTariff = tariffRepository.findById(2L);
        assertFalse(nonExistingTariff.isPresent());

        // Ejemplo 3: Guardar y buscar un registro adicional
        TariffEntity tariff2 = new TariffEntity(2L, 1500L, 3000L);
        tariffRepository.save(tariff2);
        Optional<TariffEntity> foundTariff2 = tariffRepository.findById(2L);
        assertTrue(foundTariff2.isPresent());
        assertEquals(1500L, foundTariff2.get().getDailyTariff());
        assertEquals(3000L, foundTariff2.get().getDelayTariff());
    }

    @Test
    void testCountTariffs() {
        // Ejemplo 1: Sin registros, el conteo debe ser 0
        long count = tariffRepository.count();
        assertEquals(0L, count);

        // Ejemplo 2: Insertar un registro y verificar el conteo
        TariffEntity tariff = new TariffEntity(1L, 1000L, 2000L);
        tariffRepository.save(tariff);
        count = tariffRepository.count();
        assertEquals(1L, count);

        // Ejemplo 3: Insertar múltiples registros y verificar el conteo
        TariffEntity tariff2 = new TariffEntity(2L, 1500L, 2500L);
        TariffEntity tariff3 = new TariffEntity(3L, 2000L, 3000L);
        tariffRepository.save(tariff2);
        tariffRepository.save(tariff3);
        count = tariffRepository.count();
        assertEquals(3L, count); // Verifica que hay 3 registros ahora
    }
}
