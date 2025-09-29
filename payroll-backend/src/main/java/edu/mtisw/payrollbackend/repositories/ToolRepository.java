package edu.mtisw.payrollbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.mtisw.payrollbackend.entities.ToolEntity;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {

    public List<ToolEntity> findByName(String Name);

    public List<ToolEntity> findByStatus(int status);

    public List<ToolEntity> findByCategory(String category);

    public List<ToolEntity> findByNameAndStatus(String name, int status);
}
