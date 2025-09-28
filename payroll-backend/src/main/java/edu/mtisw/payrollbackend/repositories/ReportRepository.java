package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Query("SELECT r FROM ReportEntity r WHERE r.creationDate BETWEEN :startDate AND :endDate")
    public List<ReportEntity> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}