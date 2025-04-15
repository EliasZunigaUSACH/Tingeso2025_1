package edu.mtisw.payrollbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.mtisw.payrollbackend.entities.KartEntity;

@Repository
public interface KartRepository extends JpaRepository<KartEntity, Long> {
    KartEntity findById(String id);
    KartEntity findAll(String name);
}
