package edu.mtisw.payrollbackend.entities;

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
    private Date date;
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

