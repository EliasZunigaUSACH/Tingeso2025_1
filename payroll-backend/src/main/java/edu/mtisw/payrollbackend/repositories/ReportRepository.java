package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    public List<ReportEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);
}