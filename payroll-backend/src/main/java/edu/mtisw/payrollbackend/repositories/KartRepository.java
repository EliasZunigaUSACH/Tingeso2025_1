package edu.mtisw.payrollbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import edu.mtisw.payrollbackend.entities.KartEntity;

@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {
    public KartEntity findById(String id);

    @Query(value = "SELECT * FROM karts WHERE karts.id = :id", nativeQuery = true)
    KartEntity findByIdNativeQuery(@Param("id") String id);
}
