package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KardexRegisterServiceTest {

    @Mock
    private KardexRegisterRepository kardexRegisterRepository;

    @InjectMocks
    private KardexRegisterService kardexRegisterService;

    private KardexRegisterEntity sample1;
    private KardexRegisterEntity sample2;
    private KardexRegisterEntity sample3;

    @BeforeEach
    void setUp() {
        sample1 = new KardexRegisterEntity(
                1L,
                "INGRESO",
                1,
                null,
                LocalDate.of(2023, Month.JANUARY, 10),
                100L,
                "Cliente 1",
                200L,
                "Herramienta 1"
        );

        sample2 = new KardexRegisterEntity(
                2L,
                "EGRESO",
                2,
                10L,
                LocalDate.of(2023, Month.FEBRUARY, 15),
                101L,
                "Cliente 2",
                201L,
                "Herramienta 2"
        );

        sample3 = new KardexRegisterEntity(
                3L,
                "INGRESO",
                1,
                null,
                LocalDate.of(2023, Month.MARCH, 20),
                102L,
                "Cliente 3",
                200L,
                "Herramienta 1"
        );
    }

    // -------------------------------------------------------------------------
    // getKardexRegisters()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getKardexRegisters - lista con varios registros")
    void getKardexRegisters_variosRegistros() {
        when(kardexRegisterRepository.findAll()).thenReturn(List.of(sample1, sample2, sample3));

        List<KardexRegisterEntity> result = kardexRegisterService.getKardexRegisters();

        assertEquals(3, result.size());
        assertTrue(result.contains(sample1));
        verify(kardexRegisterRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getKardexRegisters - lista vacía")
    void getKardexRegisters_listaVacia() {
        when(kardexRegisterRepository.findAll()).thenReturn(Collections.emptyList());

        List<KardexRegisterEntity> result = kardexRegisterService.getKardexRegisters();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(kardexRegisterRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getKardexRegisters - lista con un solo registro")
    void getKardexRegisters_unSoloRegistro() {
        when(kardexRegisterRepository.findAll()).thenReturn(List.of(sample1));

        List<KardexRegisterEntity> result = kardexRegisterService.getKardexRegisters();

        assertEquals(1, result.size());
        assertEquals(sample1.getId(), result.get(0).getId());
        verify(kardexRegisterRepository).findAll();
    }

    // -------------------------------------------------------------------------
    // getKardexRegisterById(Long id)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getKardexRegisterById - ID existente")
    void getKardexRegisterById_existente() {
        when(kardexRegisterRepository.findById(1L)).thenReturn(Optional.of(sample1));

        KardexRegisterEntity result = kardexRegisterService.getKardexRegisterById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(kardexRegisterRepository).findById(1L);
    }

    @Test
    @DisplayName("getKardexRegisterById - otro ID existente")
    void getKardexRegisterById_otroExistente() {
        when(kardexRegisterRepository.findById(2L)).thenReturn(Optional.of(sample2));

        KardexRegisterEntity result = kardexRegisterService.getKardexRegisterById(2L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(kardexRegisterRepository).findById(2L);
    }

    @Test
    @DisplayName("getKardexRegisterById - Optional vacío (lanza NoSuchElementException)")
    void getKardexRegisterById_noExistente() {
        when(kardexRegisterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> kardexRegisterService.getKardexRegisterById(99L));

        verify(kardexRegisterRepository).findById(99L);
    }

    // -------------------------------------------------------------------------
    // getKardexRegisterInDateRange(String startDate, String endDate)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getKardexRegisterInDateRange - rango completo (sin fechas)")
    void getKardexRegisterInDateRange_sinFechas() {
        LocalDate start = LocalDate.of(0, Month.JANUARY, 1);
        LocalDate end = LocalDate.of(9999, Month.DECEMBER, 31);

        when(kardexRegisterRepository.findByDateBetween(start, end))
                .thenReturn(List.of(sample1, sample2, sample3));

        List<KardexRegisterEntity> result =
                kardexRegisterService.getKardexRegisterInDateRange("", "");

        assertEquals(3, result.size());
        verify(kardexRegisterRepository).findByDateBetween(start, end);
    }

    @Test
    @DisplayName("getKardexRegisterInDateRange - solo fecha inicio")
    void getKardexRegisterInDateRange_soloInicio() {
        String startStr = "2023-02-01";
        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.of(9999, Month.DECEMBER, 31);

        when(kardexRegisterRepository.findByDateBetween(start, end))
                .thenReturn(List.of(sample2, sample3));

        List<KardexRegisterEntity> result =
                kardexRegisterService.getKardexRegisterInDateRange(startStr, "");

        assertEquals(2, result.size());
        assertTrue(result.contains(sample2));
        assertTrue(result.contains(sample3));
        verify(kardexRegisterRepository).findByDateBetween(start, end);
    }

    @Test
    @DisplayName("getKardexRegisterInDateRange - rango acotado con inicio y fin")
    void getKardexRegisterInDateRange_inicioYFin() {
        String startStr = "2023-01-01";
        String endStr = "2023-02-28";
        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);

        when(kardexRegisterRepository.findByDateBetween(start, end))
                .thenReturn(List.of(sample1, sample2));

        List<KardexRegisterEntity> result =
                kardexRegisterService.getKardexRegisterInDateRange(startStr, endStr);

        assertEquals(2, result.size());
        assertTrue(result.contains(sample1));
        assertTrue(result.contains(sample2));
        assertFalse(result.contains(sample3));
        verify(kardexRegisterRepository).findByDateBetween(start, end);
    }

    // -------------------------------------------------------------------------
    // getKardexRegisterByToolName(Long toolId)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getKardexRegisterByToolName - toolId con múltiples registros")
    void getKardexRegisterByToolName_varios() {
        Long toolId = 200L;
        when(kardexRegisterRepository.findByToolId(toolId))
                .thenReturn(List.of(sample1, sample3));

        List<KardexRegisterEntity> result =
                kardexRegisterService.getKardexRegisterByToolName(toolId);

        assertEquals(2, result.size());
        verify(kardexRegisterRepository).findByToolId(toolId);
    }

    @Test
    @DisplayName("getKardexRegisterByToolName - toolId con un registro")
    void getKardexRegisterByToolName_uno() {
        Long toolId = 201L;
        when(kardexRegisterRepository.findByToolId(toolId))
                .thenReturn(List.of(sample2));

        List<KardexRegisterEntity> result =
                kardexRegisterService.getKardexRegisterByToolName(toolId);

        assertEquals(1, result.size());
        assertEquals(toolId, result.get(0).getToolId());
        verify(kardexRegisterRepository).findByToolId(toolId);
    }

    @Test
    @DisplayName("getKardexRegisterByToolName - toolId sin registros")
    void getKardexRegisterByToolName_sinRegistros() {
        Long toolId = 999L;
        when(kardexRegisterRepository.findByToolId(toolId))
                .thenReturn(Collections.emptyList());

        List<KardexRegisterEntity> result =
                kardexRegisterService.getKardexRegisterByToolName(toolId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(kardexRegisterRepository).findByToolId(toolId);
    }

    // -------------------------------------------------------------------------
    // deleteKardexRegister(Long id)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteKardexRegister - borrado exitoso")
    void deleteKardexRegister_exitoso() throws Exception {
        Long id = 1L;
        doNothing().when(kardexRegisterRepository).deleteById(id);

        boolean result = kardexRegisterService.deleteKardexRegister(id);

        assertTrue(result);
        verify(kardexRegisterRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteKardexRegister - borrado de otro ID exitoso")
    void deleteKardexRegister_otroExitoso() throws Exception {
        Long id = 2L;
        doNothing().when(kardexRegisterRepository).deleteById(id);

        boolean result = kardexRegisterService.deleteKardexRegister(id);

        assertTrue(result);
        verify(kardexRegisterRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteKardexRegister - error en repositorio lanza Exception")
    void deleteKardexRegister_errorRepositorio() {
        Long id = 99L;
        doThrow(new RuntimeException("Error al eliminar"))
                .when(kardexRegisterRepository).deleteById(id);

        Exception ex = assertThrows(Exception.class,
                () -> kardexRegisterService.deleteKardexRegister(id));

        assertEquals("Error al eliminar", ex.getMessage());
        verify(kardexRegisterRepository).deleteById(id);
    }
}
