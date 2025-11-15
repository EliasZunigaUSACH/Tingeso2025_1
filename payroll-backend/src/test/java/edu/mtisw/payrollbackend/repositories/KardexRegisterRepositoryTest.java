package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class KardexRegisterRepositoryTest {

    @Autowired
    private KardexRegisterRepository kardexRegisterRepository;

    private KardexRegisterEntity createKardexRegister(
            String movement,
            int typeRelated,
            Long loanId,
            LocalDate date,
            Long clientId,
            String clientName,
            Long toolId,
            String toolName
    ) {
        KardexRegisterEntity entity = new KardexRegisterEntity();
        entity.setMovement(movement);
        entity.setTypeRelated(typeRelated);
        entity.setLoanId(loanId);
        entity.setDate(date);
        entity.setClientId(clientId);
        entity.setClientName(clientName);
        entity.setToolId(toolId);
        entity.setToolName(toolName);
        return kardexRegisterRepository.save(entity);
    }

    // -------------------------------------------------------------------------
    // Tests para findByToolId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByToolId: devuelve todos los registros asociados a un toolId")
    void findByToolId_returnsAllRegistersForGivenToolId() {
        Long toolId = 1L;

        createKardexRegister("ENTRADA", 1, null, LocalDate.now(), 100L, "Cliente 1", toolId, "Martillo");
        createKardexRegister("SALIDA", 1, null, LocalDate.now().plusDays(1), 101L, "Cliente 2", toolId, "Martillo");
        // Otro registro con distinto toolId
        createKardexRegister("ENTRADA", 1, null, LocalDate.now(), 102L, "Cliente 3", 2L, "Taladro");

        List<KardexRegisterEntity> result = kardexRegisterRepository.findByToolId(toolId);

        assertThat(result)
                .hasSize(2)
                .allMatch(r -> r.getToolId().equals(toolId));
    }

    @Test
    @DisplayName("findByToolId: devuelve lista vacía cuando no hay registros para ese toolId")
    void findByToolId_returnsEmptyListWhenNoRegisters() {
        // Se guarda un registro con otro toolId
        createKardexRegister("ENTRADA", 1, null, LocalDate.now(), 100L, "Cliente 1", 5L, "Martillo");

        List<KardexRegisterEntity> result = kardexRegisterRepository.findByToolId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByToolId: diferencia registros entre varios toolId")
    void findByToolId_filtersCorrectlyBetweenMultipleToolIds() {
        Long toolId1 = 10L;
        Long toolId2 = 20L;

        createKardexRegister("ENTRADA", 1, null, LocalDate.now(), 100L, "Cliente 1", toolId1, "Martillo");
        createKardexRegister("SALIDA", 1, null, LocalDate.now(), 101L, "Cliente 2", toolId2, "Taladro");
        createKardexRegister("ENTRADA", 1, null, LocalDate.now(), 102L, "Cliente 3", toolId1, "Martillo");

        List<KardexRegisterEntity> resultTool1 = kardexRegisterRepository.findByToolId(toolId1);
        List<KardexRegisterEntity> resultTool2 = kardexRegisterRepository.findByToolId(toolId2);

        assertThat(resultTool1)
                .hasSize(2)
                .allMatch(r -> r.getToolId().equals(toolId1));

        assertThat(resultTool2)
                .hasSize(1)
                .allMatch(r -> r.getToolId().equals(toolId2));
    }

    // -------------------------------------------------------------------------
    // Tests para findByDateBetween
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByDateBetween: devuelve registros dentro del rango de fechas (caso general)")
    void findByDateBetween_returnsRegistersWithinRange() {
        LocalDate base = LocalDate.of(2024, 1, 10);

        createKardexRegister("ENTRADA", 1, null, base.minusDays(1), 100L, "Cliente 1", 1L, "Martillo"); // fuera rango
        KardexRegisterEntity r1 = createKardexRegister("ENTRADA", 1, null, base, 101L, "Cliente 2", 1L, "Martillo");
        KardexRegisterEntity r2 = createKardexRegister("SALIDA", 1, null, base.plusDays(1), 102L, "Cliente 3", 2L, "Taladro");
        KardexRegisterEntity r3 = createKardexRegister("ENTRADA", 1, null, base.plusDays(2), 103L, "Cliente 4", 3L, "Llave");
        createKardexRegister("SALIDA", 1, null, base.plusDays(3), 104L, "Cliente 5", 4L, "Sierra"); // fuera rango

        LocalDate start = base;
        LocalDate end = base.plusDays(2);

        List<KardexRegisterEntity> result = kardexRegisterRepository.findByDateBetween(start, end);

        assertThat(result)
                .extracting(KardexRegisterEntity::getId)
                .containsExactlyInAnyOrder(r1.getId(), r2.getId(), r3.getId());
    }

    @Test
    @DisplayName("findByDateBetween: devuelve lista vacía cuando ninguna fecha cae en el rango")
    void findByDateBetween_returnsEmptyWhenNoDatesInRange() {
        LocalDate base = LocalDate.of(2024, 5, 1);

        createKardexRegister("ENTRADA", 1, null, base.minusDays(10), 100L, "Cliente 1", 1L, "Martillo");
        createKardexRegister("SALIDA", 1, null, base.plusDays(10), 101L, "Cliente 2", 2L, "Taladro");

        LocalDate start = base.minusDays(5);
        LocalDate end = base.minusDays(1);

        List<KardexRegisterEntity> result = kardexRegisterRepository.findByDateBetween(start, end);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByDateBetween: incluye correctamente los límites del rango (start y end inclusive)")
    void findByDateBetween_includesRangeBounds() {
        LocalDate start = LocalDate.of(2024, 3, 1);
        LocalDate end = LocalDate.of(2024, 3, 31);

        KardexRegisterEntity firstDay = createKardexRegister(
                "ENTRADA", 1, null, start, 100L, "Cliente 1", 1L, "Martillo"
        );
        KardexRegisterEntity middleDay = createKardexRegister(
                "SALIDA", 1, null, LocalDate.of(2024, 3, 15), 101L, "Cliente 2", 2L, "Taladro"
        );
        KardexRegisterEntity lastDay = createKardexRegister(
                "ENTRADA", 1, null, end, 102L, "Cliente 3", 3L, "Llave"
        );
        // Fuera de rango por arriba
        createKardexRegister(
                "SALIDA", 1, null, end.plusDays(1), 103L, "Cliente 4", 4L, "Sierra"
        );

        List<KardexRegisterEntity> result = kardexRegisterRepository.findByDateBetween(start, end);

        assertThat(result)
                .extracting(KardexRegisterEntity::getId)
                .containsExactlyInAnyOrder(
                        firstDay.getId(),
                        middleDay.getId(),
                        lastDay.getId()
                );
    }
}
