package edu.mtisw.payrollbackend.controllers;

import com.fasterxml.jackson.databind.DatabindException;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@CrossOrigin("*")
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    @GetMapping("/")
    public ResponseEntity<List<ReservationEntity>> listReservations() {
        List<ReservationEntity> reservations = reservationService.getReservations();
        return ResponseEntity.ok(reservations);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationsById(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/")
    public ResponseEntity<ReservationEntity> saveReservation(@RequestBody ReservationEntity reservation) {
        ReservationEntity reservationNew = reservationService.saveReservation(reservation);
        return ResponseEntity.ok(reservationNew);
    }

    @GetMapping("/{date}")
    public ResponseEntity<List<ReservationEntity>> listReservationsByDate(@PathVariable("date") Date date) {
        List<ReservationEntity> reservations = reservationService.getReservationByDate(date);
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/")
    public ResponseEntity<ReservationEntity> updateReservation(@RequestBody ReservationEntity reservation){
        ReservationEntity reservationUpdated = reservationService.updateReservation(reservation);
        return ResponseEntity.ok(reservationUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReservationById(@PathVariable Long id) throws Exception {
        var isDeleted = reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}