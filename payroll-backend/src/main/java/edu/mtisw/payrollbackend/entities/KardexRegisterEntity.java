package edu.mtisw.payrollbackend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.*;

@Entity
@Table(name = "kardexRegisters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KardexRegisterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String movement;
    private int typeRelated; // 1 = tool, 2 = loan
    private Long loanId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long clientId;
    private String clientName;
    private Long toolId;
    private String toolName;
}