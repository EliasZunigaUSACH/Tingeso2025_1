package edu.mtisw.payrollbackend.entities;

import edu.mtisw.payrollbackend.entities.LoanData;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int status; // 0 = down, 1 = on repair, 2 = loaned, 3 = available

    @ElementCollection
    @CollectionTable(name = "tool_loans", joinColumns = @JoinColumn(name = "tool_id"))
    @Column(name = "history", nullable = false)
    private List<LoanData> history = new ArrayList<>();

    @Column(nullable = false)
    private Long price;
}