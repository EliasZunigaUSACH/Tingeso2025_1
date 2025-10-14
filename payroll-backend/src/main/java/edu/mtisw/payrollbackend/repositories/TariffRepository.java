package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.TariffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<TariffEntity, Long> {}