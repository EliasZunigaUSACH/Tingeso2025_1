package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    @Query("SELECT l FROM LoanEntity l WHERE l.status = :status")
    List<LoanEntity> findByStatus(@Param("status") int status);
}