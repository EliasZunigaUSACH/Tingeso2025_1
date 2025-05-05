package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private void applyDiscount(ReservationEntity reservation) {
        Long precio = reservation.getPrice();
        int totalDiscount = reservation.getPeopleQuantityDiscount()
                + reservation.getFidelityDiscount()
                + reservation.getSpecialDayDiscount()
                + reservation.getWeekendDiscount()
                + reservation.getBirthdayDiscount();
        if (totalDiscount >= 100) {
            reservation.setTotal(0L);
        } else {
            Long descuento = precio * totalDiscount / 100;
            Long total = precio - descuento;
            Long iva = (long) (total * 0.19);
            reservation.setIva(iva);
            total += iva;
            reservation.setTotal(total);
        }
    }

    private void calcSpecialDayDiscount(ReservationEntity reservation) throws IOException, ParseException, InterruptedException {
        String date = reservation.getYear() + "-" + reservation.getMonth() + "-" + reservation.getDay();
        List<String> specialDays = obtainSpecialDays();
        if (specialDays.contains(date)){
            reservation.setSpecialDayDiscount(15);
        } else {
            reservation.setSpecialDayDiscount(0);
        }
    }

    private void calcWeekendDiscount(ReservationEntity reservation) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = reservation.getYear() + "-" + reservation.getMonth() + "-" + reservation.getDay();
        Date dt = sdf.parse(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            reservation.setWeekendDiscount(10);
        } else {
            reservation.setWeekendDiscount(0);
        }
    }

    private void calculateDiscounts(ClientEntity client, ReservationEntity reservation) throws IOException, ParseException, InterruptedException {
        //Aplicación descuento por fidelidad del cliente
        switch (client.getFidelityLevel()){
            case 0: reservation.setFidelityDiscount(0);
                break;
            case 1: reservation.setFidelityDiscount(10);
                break;
            case 2: reservation.setFidelityDiscount(20);
                break;
            case 3: reservation.setFidelityDiscount(30);
                break;
        }

        //Aplicación de descuento por cantidad de personas
        int quantity = reservation.getPeopleQuantity();
        if (3 <= quantity && quantity <= 5) {
            reservation.setPeopleQuantityDiscount(10);
        } else if (6 <= quantity && quantity <= 10) {
            reservation.setPeopleQuantityDiscount(20);
        } else if (11 <= quantity && quantity <= 15) {
            reservation.setPeopleQuantityDiscount(30);
        } else {
            reservation.setPeopleQuantityDiscount(0);
        }

        //Aplicación de descuento por día feriado
        calcSpecialDayDiscount(reservation);

        //Aplicación de descuento por fin de semana
        calcWeekendDiscount(reservation);

        //Aplicación de descuento por cumpleaños
        String reservationMMdd = reservation.getMonth() + "-" + reservation.getDay();
        if (reservationMMdd.equals(client.getBirthday())) {
            reservation.setBirthdayDiscount(50);
        } else {
            reservation.setBirthdayDiscount(0);
        }
    }

    private List<String> obtainSpecialDays() {
        List<String> feriados = new ArrayList<>();
        feriados.add("2025-01-01");
        feriados.add("2025-04-18");
        feriados.add("2025-04-19");
        feriados.add("2025-05-01");
        feriados.add("2025-05-21");
        feriados.add("2025-06-20");
        feriados.add("2025-06-29");
        feriados.add("2025-07-16");
        feriados.add("2025-08-15");
        feriados.add("2025-09-18");
        feriados.add("2025-09-19");
        feriados.add("2025-10-12");
        feriados.add("2025-10-31");
        feriados.add("2025-11-01");
        feriados.add("2025-11-16");
        feriados.add("2025-12-08");
        feriados.add("2025-12-14");
        feriados.add("2025-12-25");
        return feriados;
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

        calculateDiscounts(clientRepository.findById(reservation.getClientId()).get(), reservation);
        applyDiscount(reservation);

        // Guardar reserva
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
        return reservationRepository.save(reservation);
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
        return reservationRepository.save(reservation);
    }

    public List<ReservationEntity> getReservationByDate(String year, String month, String day) {
        return (List<ReservationEntity>) reservationRepository.findByDate(year, month, day);
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