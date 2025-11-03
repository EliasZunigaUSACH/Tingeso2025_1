package edu.mtisw.payrollbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.*;

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

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isRestricted;

    @ElementCollection
    @CollectionTable(name = "client_loans", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "loans", nullable = false)
    private List<Long> loans = new ArrayList<>();

    @Column(nullable = false)
    private Long fine;
}
