package edu.mtisw.payrollbackend.entities;

import ch.qos.logback.core.net.server.Client;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private LocalDate creationDate; // formato yyyy-mm-dd
/*
    @ElementCollection
    @CollectionTable(name = "ids", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "ids", nullable = false)
    private List<Long> ids = new ArrayList<>(); // ids dependiendo del reporte

 */

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    private List<LoanEntity> activeLoans = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    private List<LoanEntity> delayedLoans = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    private List<ClientEntity> clientsWithDelayedLoans = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    private List<ToolEntity> topTools = new ArrayList<>();
}
