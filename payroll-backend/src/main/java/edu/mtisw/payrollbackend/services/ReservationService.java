package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ClientRepository clientRepository;

   @Autowired
   ReceiptService receiptService;

    public ArrayList<ReservationEntity> getReservations(){
        return (ArrayList<ReservationEntity>) reservationRepository.findAll();
    }

    public ReservationEntity saveReservation(ReservationEntity reservation) {
    if (reservation.getClientId() == null) {
        throw new RuntimeException("El ID del cliente no puede ser nulo");
    }

    Long price = 0L;
    int time = 0, duration = reservation.getTrackTime();
    try {
        // Verifica que el cliente exista
        String clientName = clientRepository.findById(reservation.getClientId())
                             .orElseThrow(() -> new RuntimeException("Cliente no encontrado."))
                             .getName();
        reservation.setClientName(clientName);

        // Determinar el precio y tiempo según la duración
        if (duration == 10) {
            price = 15000L;
            time = 30;
        } else if (duration == 15) {
            price = 20000L;
            time = 35;
        } else if (duration == 20) {
            price = 25000L;
            time = 40;
        }
        
        reservation.setReservationTime(time);
        reservation.setEndTime(reservation.getStartTime().plusMinutes(time));
        reservation.setPrice(price);

        // Guardar reserva
        ReservationEntity savedReservation = reservationRepository.save(reservation);
        /*
        // Crear recibo asociado
        try {
            ClientEntity client = clientRepository.findById(reservation.getClientId())
                               .orElseThrow(() -> new RuntimeException("Cliente no encontrado para el recibo."));
            
            receiptService.calculateReceipt(savedReservation, client);
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular el recibo: " + e.getMessage(), e);
        }
        */
        return savedReservation;
    } catch (Exception e) {
        throw new RuntimeException("Error al guardar la reserva: " + e.getMessage(), e);
    }
}

    public ReservationEntity getReservationById(Long id){
        return reservationRepository.findById(id).get();
    }

    public ReservationEntity updateReservation(ReservationEntity reservation) {
        String clientName = clientRepository.findById(reservation.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado."))
                .getName();
        reservation.setClientName(clientName);
        Long price = 0L;
        int time = 0, duration = reservation.getTrackTime();
        if (duration == 10) {
            price = 15000L;
            time = 30;
        } else if (duration == 15) {
            price = 20000L;
            time = 35;
        } else if (duration == 20) {
            price = 25000L;
            time = 40;
        }
        reservation.setPrice(price);
        reservation.setReservationTime(time);
        LocalTime inicio = reservation.getStartTime();
        reservation.setEndTime(inicio.plusMinutes(reservation.getReservationTime()));
        ReservationEntity updatedReservation = reservationRepository.save(reservation);
        /*
        // Actualizar el recibo asociado
        try {
            ClientEntity client = clientRepository.findById(reservation.getClientId())
                           .orElseThrow(() -> new RuntimeException("Cliente no encontrado para actualizar el recibo."));
            receiptService.calculateReceipt(updatedReservation, client);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el recibo: " + e.getMessage(), e);
        }
        */
        return updatedReservation;
    }

    public List<ReservationEntity> getReservationByDate(String date) {
        return (List<ReservationEntity>) reservationRepository.findByDate(date);
    }

public boolean deleteReservation(Long id) throws Exception {
    try {
        ReservationEntity reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada para eliminar la boleta."));


        try {
            receiptService.deleteReceiptByReservationId(reservation.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la boleta asociada: " + e.getMessage(), e);
        }

        reservationRepository.deleteById(id);
        return true;
    } catch (Exception e) {
        throw new Exception("Error al eliminar la reserva: " + e.getMessage(), e);
    }
}

}