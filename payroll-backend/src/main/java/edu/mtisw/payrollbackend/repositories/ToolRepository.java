package edu.mtisw.payrollbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import edu.mtisw.payrollbackend.entities.ToolEntity;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {

    public List<ToolEntity> findByName(String Name);

    public List<ToolEntity> findByStatus(int status);

    public List<ToolEntity> findByCategory(String category);

    public List<ToolEntity> findByNameAndStatus(String name, int status);

    @Query(value = "SELECT * FROM tools ORDER BY SIZE(tool.history)", nativeQuery = true)
    List<ToolEntity> findTop10(Pageable pageable);
}
