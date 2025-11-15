package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.LoanEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    private LoanEntity createLoan(
            Long clientId,
            String clientName,
            Long toolId,
            String toolName,
            boolean isActive,
            boolean isDelayed,
            boolean toolGotDamaged
    ) {
        LoanEntity loan = new LoanEntity();
        loan.setClientId(clientId);
        loan.setClientName(clientName);
        loan.setToolId(toolId);
        loan.setToolName(toolName);
        loan.setDateStart("2024-01-01");
        loan.setDateLimit("2024-01-10");
        loan.setDateReturn(null);
        loan.setTariffPerDay(1000L);
        loan.setTotalTariff(10000L);
        loan.setDelayTariff(0L);
        loan.setDelayFine(0L);
        loan.setActive(isActive);
        loan.setDelayed(isDelayed);
        loan.setToolGotDamaged(toolGotDamaged);
        return loanRepository.save(loan);
    }

    // -------------------------------------------------------------------------
    // Tests para findByIsActiveTrueAndIsDelayedTrue (activos y atrasados)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByIsActiveTrueAndIsDelayedTrue: devuelve solo préstamos activos y atrasados")
    void findByIsActiveTrueAndIsDelayedTrue_returnsOnlyActiveAndDelayedLoans() {
        LoanEntity l1 = createLoan(1L, "Cliente 1", 10L, "Taladro", true, true, false);
        LoanEntity l2 = createLoan(2L, "Cliente 2", 11L, "Martillo", true, true, false);
        // Otros estados
        createLoan(3L, "Cliente 3", 12L, "Sierra", true, false, false);
        createLoan(4L, "Cliente 4", 13L, "Lijadora", false, true, false);

        List<LoanEntity> result = loanRepository.findByIsActiveTrueAndIsDelayedTrue();

        assertThat(result)
                .hasSize(2)
                .extracting(LoanEntity::getId)
                .containsExactlyInAnyOrder(l1.getId(), l2.getId());

        assertThat(result).allSatisfy(loan -> {
            assertThat(loan.isActive()).isTrue();
            assertThat(loan.isDelayed()).isTrue();
        });
    }

    @Test
    @DisplayName("findByIsActiveTrueAndIsDelayedTrue: lista vacía cuando no hay préstamos activos y atrasados")
    void findByIsActiveTrueAndIsDelayedTrue_returnsEmptyWhenNoActiveDelayedLoans() {
        // Todos o bien inactivos o no atrasados
        createLoan(1L, "Cliente 1", 10L, "Taladro", true, false, false);
        createLoan(2L, "Cliente 2", 11L, "Martillo", false, true, false);
        createLoan(3L, "Cliente 3", 12L, "Sierra", false, false, false);

        List<LoanEntity> result = loanRepository.findByIsActiveTrueAndIsDelayedTrue();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIsActiveTrueAndIsDelayedTrue: distingue correctamente entre combinaciones de flags")
    void findByIsActiveTrueAndIsDelayedTrue_filtersCorrectlyAcrossFlagCombinations() {
        LoanEntity activeDelayed1 = createLoan(1L, "Cliente 1", 10L, "Taladro", true, true, false);
        LoanEntity activeDelayed2 = createLoan(2L, "Cliente 2", 11L, "Martillo", true, true, true);
        // Otros estados
        createLoan(3L, "Cliente 3", 12L, "Sierra", true, false, false);  // activo, no atrasado
        createLoan(4L, "Cliente 4", 13L, "Lijadora", false, true, false); // inactivo, atrasado
        createLoan(5L, "Cliente 5", 14L, "Pulidora", false, false, false); // inactivo, no atrasado

        List<LoanEntity> result = loanRepository.findByIsActiveTrueAndIsDelayedTrue();

        assertThat(result)
                .hasSize(2)
                .extracting(LoanEntity::getId)
                .containsExactlyInAnyOrder(activeDelayed1.getId(), activeDelayed2.getId());
    }

    // -------------------------------------------------------------------------
    // Tests para findByIsActiveTrueAndIsDelayedFalse (activos y sin atraso)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByIsActiveTrueAndIsDelayedFalse: devuelve solo préstamos activos sin atraso")
    void findByIsActiveTrueAndIsDelayedFalse_returnsOnlyActiveAndNotDelayedLoans() {
        LoanEntity l1 = createLoan(1L, "Cliente 1", 10L, "Taladro", true, false, false);
        LoanEntity l2 = createLoan(2L, "Cliente 2", 11L, "Martillo", true, false, true);
        // Otros estados
        createLoan(3L, "Cliente 3", 12L, "Sierra", true, true, false);
        createLoan(4L, "Cliente 4", 13L, "Lijadora", false, false, false);

        List<LoanEntity> result = loanRepository.findByIsActiveTrueAndIsDelayedFalse();

        assertThat(result)
                .hasSize(2)
                .extracting(LoanEntity::getId)
                .containsExactlyInAnyOrder(l1.getId(), l2.getId());

        assertThat(result).allSatisfy(loan -> {
            assertThat(loan.isActive()).isTrue();
            assertThat(loan.isDelayed()).isFalse();
        });
    }

    @Test
    @DisplayName("findByIsActiveTrueAndIsDelayedFalse: lista vacía cuando no hay préstamos activos sin atraso")
    void findByIsActiveTrueAndIsDelayedFalse_returnsEmptyWhenNoActiveNotDelayedLoans() {
        // Ninguno cumple activo=true y delayed=false a la vez
        createLoan(1L, "Cliente 1", 10L, "Taladro", false, false, false);
        createLoan(2L, "Cliente 2", 11L, "Martillo", false, true, false);
        createLoan(3L, "Cliente 3", 12L, "Sierra", true, true, false);

        List<LoanEntity> result = loanRepository.findByIsActiveTrueAndIsDelayedFalse();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIsActiveTrueAndIsDelayedFalse: distingue correctamente entre combinaciones de flags")
    void findByIsActiveTrueAndIsDelayedFalse_filtersCorrectlyAcrossFlagCombinations() {
        LoanEntity activeNotDelayed1 = createLoan(1L, "Cliente 1", 10L, "Taladro", true, false, false);
        LoanEntity activeNotDelayed2 = createLoan(2L, "Cliente 2", 11L, "Martillo", true, false, false);
        // Otros estados
        createLoan(3L, "Cliente 3", 12L, "Sierra", true, true, false);   // activo, atrasado
        createLoan(4L, "Cliente 4", 13L, "Lijadora", false, false, false); // inactivo, no atrasado
        createLoan(5L, "Cliente 5", 14L, "Pulidora", false, true, false); // inactivo, atrasado

        List<LoanEntity> result = loanRepository.findByIsActiveTrueAndIsDelayedFalse();

        assertThat(result)
                .hasSize(2)
                .extracting(LoanEntity::getId)
                .containsExactlyInAnyOrder(activeNotDelayed1.getId(), activeNotDelayed2.getId());
    }
}
