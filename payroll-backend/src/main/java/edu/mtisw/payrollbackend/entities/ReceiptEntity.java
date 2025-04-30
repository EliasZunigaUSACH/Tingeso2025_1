package edu.mtisw.payrollbackend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    private Long reservationId;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Asegurar formato
    private String date;

    private LocalTime time;
    private Long clientId;
    private int peopleQuantityDiscount;
    private int fidelityDiscount;
    private int specialDayDiscount;
    private int weekendDiscount;
    private int birthdayDiscount;
    private Long iva;
    private Long total;
}

