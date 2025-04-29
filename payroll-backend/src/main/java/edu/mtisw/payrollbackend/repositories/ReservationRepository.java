package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    public List<ReservationEntity> findByClientId(Long id);
    
    List<ReservationEntity> findByDate(String date);
    @Query(value = "SELECT * FROM reservations WHERE reservations.id = :id ORDER BY reservations.date, reservations.startTime", nativeQuery = true)
    List<ReservationEntity> getReservationByIdNQ(@Param("id") Long id);
}
