package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {
    @Query(value = "SELECT * FROM receipts WHERE receipts.id = :id", nativeQuery = true)
    List<ReceiptEntity> getReceiptsByIdQN(@Param("id") Long id);
}