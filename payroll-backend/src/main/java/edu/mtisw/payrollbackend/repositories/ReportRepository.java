package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

}
