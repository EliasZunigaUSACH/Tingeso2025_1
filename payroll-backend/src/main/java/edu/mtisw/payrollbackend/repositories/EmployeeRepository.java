package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    public EmployeeEntity findByRut(String rut);

    List<EmployeeEntity> findByCategory(String category);
    List<EmployeeEntity> findBySalaryGreaterThan(int salary);
    List<EmployeeEntity> findByChildrenBetween(Integer startChildren, Integer endChildren);

    @Query(value = "SELECT * FROM employees WHERE employees.rut = :rut", nativeQuery = true)
    EmployeeEntity findByRutNativeQuery(@Param("rut") String rut);
}