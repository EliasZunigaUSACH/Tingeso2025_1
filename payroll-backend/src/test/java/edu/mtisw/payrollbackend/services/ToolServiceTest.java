package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.repositories.ToolRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToolServiceTest {

    @InjectMocks
    private ToolService toolService;

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private KardexRegisterRepository kardexRegisterRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTool() {
        // Ejemplo 1: Guardar herramienta válida
        ToolEntity tool1 = new ToolEntity();
        tool1.setId(1L);
        tool1.setName("Martillo");

        when(toolRepository.save(tool1)).thenReturn(tool1);

        ToolEntity result1 = toolService.saveTool(tool1);

        assertNotNull(result1);
        assertEquals("Martillo", result1.getName());
        verify(toolRepository).save(tool1);

        // Ejemplo 2: Guardar herramienta con historial vacío
        ToolEntity tool2 = new ToolEntity();
        tool2.setId(2L);
        tool2.setName("Llave Inglesa");
        tool2.setHistory(new ArrayList<>());

        when(toolRepository.save(tool2)).thenReturn(tool2);
        ToolEntity result2 = toolService.saveTool(tool2);

        assertNotNull(result2);
        assertEquals(0, result2.getHistory().size());

        // Ejemplo 3: Guardar herramienta y verificar movimiento Kardex
        verify(kardexRegisterRepository, times(2)).save(any());
    }

    @Test
    void testGetAllTools() {
        // Ejemplo 1: Lista vacía
        when(toolRepository.findAll()).thenReturn(Collections.emptyList());

        List<ToolEntity> result1 = toolService.getAllTools();

        assertTrue(result1.isEmpty());
        verify(toolRepository).findAll();

        // Ejemplo 2: Lista con una herramienta
        ToolEntity tool = new ToolEntity();
        tool.setId(1L);
        tool.setName("Taladro");

        when(toolRepository.findAll()).thenReturn(Collections.singletonList(tool));

        List<ToolEntity> result2 = toolService.getAllTools();

        assertEquals(1, result2.size());
        assertEquals("Taladro", result2.get(0).getName());

        // Ejemplo 3: Lista con varias herramientas
        ToolEntity tool2 = new ToolEntity();
        tool2.setId(2L);
        tool2.setName("Destornillador");

        when(toolRepository.findAll()).thenReturn(Arrays.asList(tool, tool2));

        List<ToolEntity> result3 = toolService.getAllTools();
        assertEquals(2, result3.size());
    }

    @Test
    void testGetToolById() {
        // Ejemplo 1: Herramienta encontrada por ID
        ToolEntity tool = new ToolEntity();
        tool.setId(1L);
        tool.setName("Martillo");

        when(toolRepository.findById(1L)).thenReturn(Optional.of(tool));

        ToolEntity result1 = toolService.getToolById(1L);

        assertNotNull(result1);
        assertEquals("Martillo", result1.getName());

        // Ejemplo 2: Herramienta no encontrada
        when(toolRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> toolService.getToolById(2L));
        assertEquals("No value present", exception.getMessage());

        // Ejemplo 3: Verificar interacción con el repositorio
        verify(toolRepository, times(2)).findById(anyLong());
    }

    @Test
    void testUpdateTool() {
        // Ejemplo 1: Actualizar herramienta con nueva información
        ToolEntity oldTool = new ToolEntity();
        oldTool.setId(1L);
        oldTool.setName("Taladro");
        oldTool.setStatus(1);

        ToolEntity updatedTool = new ToolEntity();
        updatedTool.setId(1L);
        updatedTool.setName("Taladro Pro");
        updatedTool.setStatus(2);

        when(toolRepository.findById(1L)).thenReturn(Optional.of(oldTool));
        when(toolRepository.save(any())).thenReturn(updatedTool);

        ToolEntity result1 = toolService.updateTool(updatedTool);

        assertNotNull(result1);
        assertEquals("Taladro Pro", result1.getName());
        assertEquals(2, result1.getStatus());

        // Ejemplo 2: Actualizar herramienta inexistente
        ToolEntity nonExistentTool = new ToolEntity();
        nonExistentTool.setId(2L);
        nonExistentTool.setName("Llave"); // Nueva herramienta ficticia

        when(toolRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> toolService.updateTool(nonExistentTool));
        assertEquals("No value present", exception.getMessage());

        // Ejemplo 3: Registrar cambios en Kardex
        verify(kardexRegisterRepository, times(1)).save(any());
    }

    @Test
    void testDeleteTool() throws Exception {
        // Ejemplo 1: Borrar herramienta existente
        doNothing().when(toolRepository).deleteById(1L);

        boolean result1 = toolService.deleteTool(1L);

        assertTrue(result1);
        verify(toolRepository).deleteById(1L);

        // Ejemplo 2: Intentar borrar una herramienta no existente lanza excepción
        doThrow(new RuntimeException("No se pudo borrar herramienta")).when(toolRepository).deleteById(2L);

        Exception exception = assertThrows(Exception.class, () -> toolService.deleteTool(2L));
        assertEquals("No se pudo borrar herramienta", exception.getMessage());

        // Ejemplo 3: Validar interacción correcta con el repositorio
        verify(toolRepository, times(2)).deleteById(anyLong());
    }

    @Test
    void testGetStock() {
        // Ejemplo 1: Herramientas disponibles
        when(toolRepository.findByNameAndStatus("Martillo", 3)).thenReturn(Arrays.asList(new ToolEntity(), new ToolEntity()));

        int stock1 = toolService.getStock("Martillo");

        assertEquals(2, stock1);

        // Ejemplo 2: Sin herramientas disponibles
        when(toolRepository.findByNameAndStatus("Sierra", 3)).thenReturn(Collections.emptyList());

        int stock2 = toolService.getStock("Sierra");

        assertEquals(0, stock2);

        // Ejemplo 3: Verificar interacción
        verify(toolRepository, times(2)).findByNameAndStatus(anyString(), eq(3));
    }
}
