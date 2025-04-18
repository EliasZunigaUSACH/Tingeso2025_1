package edu.mtisw.payrollbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import edu.mtisw.payrollbackend.entities.KartEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {

    List<KartEntity> findByStatus(String status);
    List<KartEntity> findByAvailable(boolean available);

    @Query(value = "SELECT * FROM karts WHERE karts.id = :id", nativeQuery = true)
    KartEntity findByIdNativeQuery(@Param("id") Long id);
}
