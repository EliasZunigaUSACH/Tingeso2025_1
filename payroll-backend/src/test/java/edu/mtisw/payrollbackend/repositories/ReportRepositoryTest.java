package edu.mtisw.payrollbackend.repositories;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import edu.mtisw.payrollbackend.entities.ReportEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ReportRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void testSaveReport() {
        // Ejemplo 1: Guardar reporte con fecha y listas vacías
        ReportEntity report1 = new ReportEntity();
        report1.setCreationDate("2024-01-01");
        report1.setActiveLoans(Collections.emptyList());
        report1.setDelayedLoans(Collections.emptyList());
        report1.setClientsWithDelayedLoans(Collections.emptyList());
        report1.setTopTools(Collections.emptyList());

        ReportEntity saved1 = reportRepository.save(report1);

        assertThat(saved1.getId()).isNotNull();
        assertThat(saved1.getCreationDate()).isEqualTo("2024-01-01");

        // Ejemplo 2: Guardar reporte con algunos datos
        ReportEntity report2 = new ReportEntity();
        report2.setCreationDate("2024-02-01");
        report2.setClientsWithDelayedLoans(List.of("Juan", "María"));
        report2.setTopTools(List.of("Martillo", "Sierra"));

        ReportEntity saved2 = reportRepository.save(report2);

        assertThat(saved2.getId()).isNotNull();
        assertThat(saved2.getClientsWithDelayedLoans()).containsExactly("Juan", "María");
        assertThat(saved2.getTopTools()).contains("Martillo", "Sierra");

        // Ejemplo 3: Verificar que ambos fueron persistidos
        List<ReportEntity> all = reportRepository.findAll();
        assertThat(all)
                .extracting(ReportEntity::getCreationDate)
                .contains("2024-01-01", "2024-02-01");
    }

    @Test
    void testFindById() {
        // Ejemplo 1: Encontrar reporte existente
        ReportEntity report1 = new ReportEntity();
        report1.setCreationDate("2024-03-01");
        report1 = entityManager.persistAndFlush(report1);

        Optional<ReportEntity> found1 = reportRepository.findById(report1.getId());

        assertThat(found1).isPresent();
        assertThat(found1.get().getCreationDate()).isEqualTo("2024-03-01");

        // Ejemplo 2: Buscar ID inexistente
        Optional<ReportEntity> found2 = reportRepository.findById(99999L);
        assertThat(found2).isNotPresent();

        // Ejemplo 3: Varios registros y búsqueda específica
        ReportEntity report2 = new ReportEntity();
        report2.setCreationDate("2024-03-02");
        report2 = entityManager.persistAndFlush(report2);

        ReportEntity report3 = new ReportEntity();
        report3.setCreationDate("2024-03-03");
        report3 = entityManager.persistAndFlush(report3);

        Optional<ReportEntity> found3 = reportRepository.findById(report3.getId());

        assertThat(found3).isPresent();
        assertThat(found3.get().getCreationDate()).isEqualTo("2024-03-03");
    }

    @Test
    void testFindAll() {
        // Ejemplo 1: Sin registros
        List<ReportEntity> emptyList = reportRepository.findAll();
        assertThat(emptyList).isEmpty();

        // Ejemplo 2: Un solo registro
        ReportEntity report1 = new ReportEntity();
        report1.setCreationDate("2024-04-01");
        entityManager.persistAndFlush(report1);

        List<ReportEntity> oneReportList = reportRepository.findAll();
        assertThat(oneReportList).hasSize(1);
        assertThat(oneReportList.get(0).getCreationDate()).isEqualTo("2024-04-01");

        // Ejemplo 3: Múltiples registros
        ReportEntity report2 = new ReportEntity();
        report2.setCreationDate("2024-04-02");
        entityManager.persistAndFlush(report2);

        ReportEntity report3 = new ReportEntity();
        report3.setCreationDate("2024-04-03");
        entityManager.persistAndFlush(report3);

        List<ReportEntity> allReports = reportRepository.findAll();
        assertThat(allReports).hasSize(3);
        assertThat(allReports)
                .extracting(ReportEntity::getCreationDate)
                .contains("2024-04-01", "2024-04-02", "2024-04-03");
    }

    @Test
    void testDeleteById() {
        // Ejemplo 1: Eliminar reporte existente
        ReportEntity report1 = new ReportEntity();
        report1.setCreationDate("2024-05-01");
        report1 = entityManager.persistAndFlush(report1);

        reportRepository.deleteById(report1.getId());

        Optional<ReportEntity> deleted1 = reportRepository.findById(report1.getId());
        assertThat(deleted1).isNotPresent();

        // Ejemplo 2: Eliminar ID inexistente (no debe lanzar excepción)
        long countBefore = reportRepository.count();
        reportRepository.deleteById(99999L);
        long countAfter = reportRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);

        // Ejemplo 3: Eliminar uno de varios registros
        ReportEntity report2 = new ReportEntity();
        report2.setCreationDate("2024-05-02");
        report2 = entityManager.persistAndFlush(report2);

        ReportEntity report3 = new ReportEntity();
        report3.setCreationDate("2024-05-03");
        report3 = entityManager.persistAndFlush(report3);

        long countBeforeDelete = reportRepository.count();
        reportRepository.deleteById(report2.getId());
        long countAfterDelete = reportRepository.count();

        assertThat(countAfterDelete).isEqualTo(countBeforeDelete - 1);
        assertThat(reportRepository.findById(report2.getId())).isNotPresent();
        assertThat(reportRepository.findById(report3.getId())).isPresent();
    }

    @Test
    void testFindByCreationDateBetween() {
        // Datos de prueba
        ReportEntity report1 = new ReportEntity();
        report1.setCreationDate("2024-06-01");
        entityManager.persist(report1);

        ReportEntity report2 = new ReportEntity();
        report2.setCreationDate("2024-06-10");
        entityManager.persist(report2);

        ReportEntity report3 = new ReportEntity();
        report3.setCreationDate("2024-06-20");
        entityManager.persist(report3);

        entityManager.flush();

        // Ejemplo 1: Rango que incluye todos los registros
        List<ReportEntity> result1 =
                reportRepository.findByCreationDateBetween("2024-06-01", "2024-06-30");

        assertThat(result1)
                .extracting(ReportEntity::getCreationDate)
                .containsExactlyInAnyOrder("2024-06-01", "2024-06-10", "2024-06-20");

        // Ejemplo 2: Rango que incluye solo un subconjunto
        List<ReportEntity> result2 =
                reportRepository.findByCreationDateBetween("2024-06-05", "2024-06-15");

        assertThat(result2)
                .extracting(ReportEntity::getCreationDate)
                .containsExactly("2024-06-10");

        // Ejemplo 3: Rango sin resultados
        List<ReportEntity> result3 =
                reportRepository.findByCreationDateBetween("2024-07-01", "2024-07-31");

        assertThat(result3).isEmpty();
    }
}