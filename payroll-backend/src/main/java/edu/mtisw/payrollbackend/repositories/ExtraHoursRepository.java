package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ExtraHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraHoursRepository extends JpaRepository<ExtraHoursEntity, Long> {
    public List<ExtraHoursEntity> findByRut(String rut);
    @Query(value = "SELECT * FROM extra_hours WHERE extra_hours.rut = :rut AND YEAR(extra_hours.date)=:year AND MONTH(extra_hours.date)=:month", nativeQuery = true)
    List<ExtraHoursEntity> getExtraHoursByRutYearMonth(@Param("rut") String rut, @Param("year") int year, @Param("month") int month);

}
