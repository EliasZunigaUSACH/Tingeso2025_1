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

    @Column(nullable = false, name = "creation_date")
    private String creationDate; // formato yyyy-mm-dd

    @ElementCollection
    @CollectionTable(name = "report_active_loans", joinColumns = @JoinColumn(name = "report_id"))
    private List<LoanData> activeLoans = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "report_delayed_loans", joinColumns = @JoinColumn(name = "report_id"))
    private List<LoanData> delayedLoans = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "report_client_delayed_loans", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "client_names", nullable = false)
    private List<String> clientsWithDelayedLoans = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "top_tools", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "tools_loans", nullable = false)
    private List<String> topTools = new ArrayList<>();
}
