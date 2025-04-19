package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {
    @Query(value = "SELECT * FROM paychecks WHERE paychecks.year = :year AND paychecks.month = :month ORDER BY paychecks.year, paychecks.month, paychecks.rut", nativeQuery = true)
    List<ReceiptEntity> getPaychecksByYearMonth(@Param("year") int year, @Param("month") int month);

}