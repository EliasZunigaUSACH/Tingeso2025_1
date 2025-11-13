package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ToolEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ToolRepositoryTest {

    @Autowired
    private ToolRepository toolRepository;

    @BeforeEach
    void setUp() {
        // Inicialización de datos en la base de datos H2 en memoria
        ToolEntity tool1 = new ToolEntity(null, "Martillo", "Construcción", 3, List.of(), 500L);
        ToolEntity tool2 = new ToolEntity(null, "Llave Inglesa", "Mecánica", 2, List.of(), 200L);
        ToolEntity tool3 = new ToolEntity(null, "Sierra", "Construcción", 3, List.of(), 800L);
        ToolEntity tool4 = new ToolEntity(null, "Taladro", "Herramientas eléctricas", 1, List.of(), 1500L);

        toolRepository.save(tool1);
        toolRepository.save(tool2);
        toolRepository.save(tool3);
        toolRepository.save(tool4);
    }

    @Test
    void testFindByStatus() {
        // Ejemplo 1: Herramientas con estado 3 (disponible)
        List<ToolEntity> availableTools = toolRepository.findByStatus(3);
        assertEquals(2, availableTools.size());
        assertTrue(availableTools.stream().anyMatch(tool -> tool.getName().equals("Martillo")));
        assertTrue(availableTools.stream().anyMatch(tool -> tool.getName().equals("Sierra")));

        // Ejemplo 2: Herramientas con estado 2 (prestado)
        List<ToolEntity> loanedTools = toolRepository.findByStatus(2);
        assertEquals(1, loanedTools.size());
        assertEquals("Llave Inglesa", loanedTools.get(0).getName());

        // Ejemplo 3: Herramientas con estado 0 (baja/no disponible)
        List<ToolEntity> downTools = toolRepository.findByStatus(0);
        assertTrue(downTools.isEmpty());
    }

    @Test
    void testFindByCategory() {
        // Ejemplo 1: Herramientas de categoría "Construcción"
        List<ToolEntity> constructionTools = toolRepository.findByCategory("Construcción");
        assertEquals(2, constructionTools.size());
        assertTrue(constructionTools.stream().anyMatch(tool -> tool.getName().equals("Martillo")));
        assertTrue(constructionTools.stream().anyMatch(tool -> tool.getName().equals("Sierra")));

        // Ejemplo 2: Herramientas de categoría "Mecánica"
        List<ToolEntity> mechanicsTools = toolRepository.findByCategory("Mecánica");
        assertEquals(1, mechanicsTools.size());
        assertEquals("Llave Inglesa", mechanicsTools.get(0).getName());

        // Ejemplo 3: Categoría no existente
        List<ToolEntity> nonExistentCategory = toolRepository.findByCategory("Hogar");
        assertTrue(nonExistentCategory.isEmpty());
    }

    @Test
    void testFindByNameAndStatus() {
        // Ejemplo 1: Buscar herramienta por nombre "Martillo" y estado 3 (disponible)
        List<ToolEntity> tools = toolRepository.findByNameAndStatus("Martillo", 3);
        assertEquals(1, tools.size());
        assertEquals("Martillo", tools.get(0).getName());

        // Ejemplo 2: Buscar herramienta por nombre "Sierra" con estado 0 (inexistente)
        tools = toolRepository.findByNameAndStatus("Sierra", 0);
        assertTrue(tools.isEmpty());

        // Ejemplo 3: Herramienta existente con estado diferente
        tools = toolRepository.findByNameAndStatus("Llave Inglesa", 2);
        assertEquals(1, tools.size());
        assertEquals("Llave Inglesa", tools.get(0).getName());
    }
}
