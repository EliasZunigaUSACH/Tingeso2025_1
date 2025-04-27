package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.MonthDay;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    List<ClientEntity> findByBirthday(String birthday);
    List<ClientEntity> findByFidelityLevel(int level);

    @Query(value = "SELECT * FROM clients WHERE clients.id = :id", nativeQuery = true)
    ClientEntity findByIdNativeQuery(@Param("id") Long id);
}