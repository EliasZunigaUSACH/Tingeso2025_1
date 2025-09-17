package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

import java.time.LocalDate;

@Repository
public interface KardexRegisterRepository extends JpaRepository<KardexRegisterEntity, Long> {
    public List<KardexRegisterEntity> findByToolName(String toolName);

    public List<KardexRegisterEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    public List<KardexRegisterEntity> findByTypeRelated(int typeRelated);
}
