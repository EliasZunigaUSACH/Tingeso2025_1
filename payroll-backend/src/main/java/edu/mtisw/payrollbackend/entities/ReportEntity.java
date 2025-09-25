package edu.mtisw.payrollbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private String name; // puede ser de clientes, herramientas o prestamos
    private int type; // 1 = client, 2 = tool, 3 = loan
    private LocalDate date;

    @ElementCollection
    private List<Long> ids = new ArrayList<>(); // ids dependiendo del reporte
}
