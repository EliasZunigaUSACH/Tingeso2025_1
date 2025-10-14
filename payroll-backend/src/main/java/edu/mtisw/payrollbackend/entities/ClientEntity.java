package edu.mtisw.payrollbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String name;
    private String phone;
    private String rut;
    private String email;
    private int status; // 0 = restricted, 1 = active

    @ElementCollection
    @CollectionTable(name = "client_loans", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "loans", nullable = false)
    private List<Long> loans = new ArrayList<>();
    private Long fine;
}
