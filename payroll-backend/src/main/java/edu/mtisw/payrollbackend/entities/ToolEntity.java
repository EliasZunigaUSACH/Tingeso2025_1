package edu.mtisw.payrollbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "tools")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ToolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private String name;
    private String category;
    private int status; // 0 = down, 1 = on repair, 2 = loaned, 3 = available

    private List<Integer> loansIds;

    private Long price;
}