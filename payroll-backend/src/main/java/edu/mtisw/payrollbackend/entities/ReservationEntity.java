package edu.mtisw.payrollbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private int peopleQuantity;
    private int trackTime;
    private int reservationTime;

    @Column(nullable = false)
    private Date date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long price;
}

