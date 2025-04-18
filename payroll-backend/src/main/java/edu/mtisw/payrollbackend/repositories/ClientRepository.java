package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Date;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    List<ClientEntity> findByBirthday(Date birthday);
    List<ClientEntity> findByFrequencyLevelClient(int level);

    @Query(value = "SELECT * FROM clients WHERE clients.id = :id", nativeQuery = true)
    ClientEntity findByIdNativeQuery(@Param("id") Long id);
}