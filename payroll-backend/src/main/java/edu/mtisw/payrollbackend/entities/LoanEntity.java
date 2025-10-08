package edu.mtisw.payrollbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long clientId;
    private String clientName;

    @Column(nullable = false)
    private Long ToolId;
    private String toolName;

    private String dateStart;
    private String dateLimit;
    private String dateReturn;

    private Long tariff; // daily price
    private Long delayTariff; // if tool gets delayed

    private int status; // 0 = terminated, 1 = active, 2 = active but with delay
    private int isDelayedReturn; // 0 = no, 1 = yes
    private int toolReturnStatus; // 0 = damaged, 1 = good condition
}

