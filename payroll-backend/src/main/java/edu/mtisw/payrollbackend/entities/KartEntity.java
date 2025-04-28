package edu.mtisw.payrollbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "karts")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class KartEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String status;
    private boolean available;
}
