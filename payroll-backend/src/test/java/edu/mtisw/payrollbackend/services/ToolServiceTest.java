package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.LoanData;
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
    private ClientService clientService;

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

    @Test
    void testGetToolsByStatus() {
        ToolEntity tool = new ToolEntity();
        tool.setId(1L);
        tool.setStatus(3); // Disponible

        when(toolRepository.findByStatus(3)).thenReturn(Collections.singletonList(tool));

        List<ToolEntity> result = toolService.getToolsByStatus(3);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getStatus());
        verify(toolRepository).findByStatus(3);
    }

    @Test
    void testGetToolsByCategory() {
        ToolEntity tool = new ToolEntity();
        tool.setId(1L);
        tool.setCategory("Manual");

        when(toolRepository.findByCategory("Manual")).thenReturn(Collections.singletonList(tool));

        List<ToolEntity> result = toolService.getToolsByCategory("Manual");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Manual", result.get(0).getCategory());
        verify(toolRepository).findByCategory("Manual");
    }

    @Test
    void testGetTop10Tools() {
        // Crear herramientas con historiales de diferentes tamaños
        ToolEntity t1 = new ToolEntity();
        t1.setId(1L);
        t1.setName("Tool1");
        t1.setHistory(Arrays.asList(new LoanData(), new LoanData())); // 2 préstamos

        ToolEntity t2 = new ToolEntity();
        t2.setId(2L);
        t2.setName("Tool2");
        t2.setHistory(Collections.singletonList(new LoanData())); // 1 préstamo

        when(toolRepository.findAll()).thenReturn(Arrays.asList(t2, t1)); // Orden original no importa

        List<String> result = toolService.getTop10Tools();

        // Debe devolver primero la que tiene más historial (t1)
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("Tool1"));
        assertTrue(result.get(1).contains("Tool2"));
    }

    @Test
    void testUpdateTool_Decommission() {
        // Ejemplo de "Dar herramienta de baja" (Status 0)
        // Escenario: Herramienta dañada, se debe cobrar al último cliente.

        // 1. Datos de la herramienta antigua y nueva
        ToolEntity oldTool = new ToolEntity();
        oldTool.setId(10L);
        oldTool.setStatus(2); // Estaba prestada
        oldTool.setPrice(5000L);

        ToolEntity newToolInput = new ToolEntity();
        newToolInput.setId(10L);
        newToolInput.setStatus(0); // Nuevo estado: Baja (0)
        newToolInput.setPrice(5000L);

        // 2. Configurar historial de préstamos para encontrar al cliente
        LoanData lastLoanData = new LoanData();
        lastLoanData.setLoanID(100L);
        List<LoanData> history = new ArrayList<>();
        history.add(lastLoanData);

        // Importante: Simular que newToolInput ya tiene el historial cargado (normalmente viene del front o DB)
        newToolInput.setHistory(history);
        // Y el updatedTool que devuelve el repo también
        ToolEntity savedTool = new ToolEntity();
        savedTool.setId(10L);
        savedTool.setStatus(0);
        savedTool.setPrice(5000L);
        savedTool.setHistory(history);

        // 3. Datos del Préstamo y Cliente
        edu.mtisw.payrollbackend.entities.LoanEntity loanEntity = new edu.mtisw.payrollbackend.entities.LoanEntity();
        loanEntity.setId(100L);
        loanEntity.setClientId(50L);
        loanEntity.setDelayed(false); // No estaba atrasado

        edu.mtisw.payrollbackend.entities.ClientEntity clientEntity = new edu.mtisw.payrollbackend.entities.ClientEntity();
        clientEntity.setId(50L);
        clientEntity.setFine(0L); // Multa inicial 0

        // 4. Mocks
        when(toolRepository.findById(10L)).thenReturn(Optional.of(oldTool));
        when(toolRepository.save(any(ToolEntity.class))).thenReturn(savedTool);
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loanEntity));
        when(clientRepository.findById(50L)).thenReturn(Optional.of(clientEntity));

        // 5. Ejecución
        ToolEntity result = toolService.updateTool(newToolInput);

        // 6. Verificaciones
        // Se debió actualizar el cliente sumando el precio de la herramienta (5000)
        assertEquals(5000L, clientEntity.getFine());
        verify(clientService).updateClient(clientEntity);

        // Se debió registrar el movimiento en Kardex como "Baja de herramienta"
        verify(kardexRegisterRepository).save(argThat(kardex ->
                kardex.getMovement().equals("Baja de herramienta") &&
                        kardex.getToolId().equals(10L)
        ));
    }
}
