package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    public List<ReservationEntity> findByClientId(Long id);
    
    List<ReservationEntity> findByYearAndMonthAndDay(String year, String month, String day);
    
    @Query(value = "SELECT * FROM reservations WHERE reservations.year = :year AND reservations.month = :month ORDER BY reservations.year, reservations.month, reservations.day, reservations.startTime", nativeQuery = true)
    List<ReservationEntity> findByYearMonth(@Param("year") String year, @Param("month") String month);
}