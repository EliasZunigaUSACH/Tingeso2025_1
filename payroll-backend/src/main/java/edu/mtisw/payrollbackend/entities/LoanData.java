package edu.mtisw.payrollbackend.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanData {
    private String loanDate;      // Fecha de préstamo
    private String dueDate;       // Fecha límite
    private String clientName;    // Nombre del cliente
    private String toolName;      // Nombre de la herramienta
    private Long toolId;          // ID de la herramienta
}
