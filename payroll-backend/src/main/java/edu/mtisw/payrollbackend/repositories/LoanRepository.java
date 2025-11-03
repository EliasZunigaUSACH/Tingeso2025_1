package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    List<LoanEntity> findByIsActiveTrueAndIsDelayedTrue(); // préstamos activos y atrasados
    List<LoanEntity> findByIsActiveTrueAndIsDelayedFalse(); // préstamos activos y sin atraso
}