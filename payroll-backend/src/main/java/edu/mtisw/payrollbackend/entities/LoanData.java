package edu.mtisw.payrollbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanData {
    @Column(nullable = false)
    private Long loanID;          // ID del préstamo

    @Column(nullable = false)
    private String loanDate;      // Fecha de préstamo

    @Column(nullable = false)
    private String dueDate;       // Fecha límite

    @Column(nullable = false)
    private String clientName;    // Nombre del cliente

    @Column(nullable = false)
    private String toolName;      // Nombre de la herramienta

    @Column(nullable = false)
    private Long dataToolId;      // ID de la herramienta

    private String returnDate;    // Fecha devolución
    private String status;        // Estado del préstamo (vigente o atrasado)
}