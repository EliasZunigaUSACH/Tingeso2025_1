package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.ReceiptRepository;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReceiptService {
    @Autowired
    ReceiptRepository receiptRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ClientRepository clientRepository;

    public ArrayList<ReceiptEntity> getReceipts(){
        return (ArrayList<ReceiptEntity>) receiptRepository.findAll();
    }

    private void applyDiscount(ReceiptEntity receipt, ReservationEntity reservation) {
        Long precio = reservation.getPrice();
        int totalDiscount = receipt.getPeopleQuantityDiscount()
                            + receipt.getFidelityDiscount()
                            + receipt.getSpecialDayDiscount()
                            + receipt.getWeekendDiscount()
                            + receipt.getBirthdayDiscount();
        if (totalDiscount >= 100) {
            receipt.setTotal(0L);
        } else {
            Long descuento = precio * totalDiscount / 100;
            Long total = precio - descuento;
            receipt.setTotal(total);
        }
    }

    private void calculateDiscounts(ReceiptEntity receipt, ClientEntity client, ReservationEntity reservation) throws IOException, ParseException, InterruptedException {
        //Aplicación descuento por fidelidad del cliente
        switch (client.getFidelityLevel()){
            case 0: receipt.setFidelityDiscount(0);
                    break;
            case 1: receipt.setFidelityDiscount(10);
                    break;
            case 2: receipt.setFidelityDiscount(20);
                    break;
            case 3: receipt.setFidelityDiscount(30);
                    break;
        }

        //Aplicación de descuento por cantidad de personas
        int quantity = reservation.getPeopleQuantity();
        if (3 <= quantity && quantity <= 5) {
            receipt.setPeopleQuantityDiscount(10);
        } else if (6 <= quantity && quantity <= 10) {
            receipt.setPeopleQuantityDiscount(20);
        } else if (11 <= quantity && quantity <= 15) {
            receipt.setPeopleQuantityDiscount(30);
        } else {
            receipt.setPeopleQuantityDiscount(0);
        }

        //Aplicación de descuento por día feriado
        calcSpecialDayDiscount(receipt, reservation);

        //Aplicación de descuento por fin de semana
        calcWeekendDiscount(receipt, reservation);

        //Aplicación de descuento por cumpleaños
        SimpleDateFormat sdfReservation = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfCompare = new SimpleDateFormat("MM-dd");
        String reservationMMdd = sdfCompare.format(sdfReservation.parse(reservation.getDate()));
        if (reservationMMdd.equals(client.getBirthday())) {
            receipt.setBirthdayDiscount(50);
        } else {
            receipt.setBirthdayDiscount(0);
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

    private void calcSpecialDayDiscount(ReceiptEntity receipt, ReservationEntity reservation) throws IOException, ParseException, InterruptedException {
        String date = reservation.getDate();
        List<String> specialDays = obtainSpecialDays();
        if (specialDays.contains(date)){
            receipt.setSpecialDayDiscount(15);
        } else {
            receipt.setSpecialDayDiscount(0);
        }
    }

    private void calcWeekendDiscount(ReceiptEntity receipt, ReservationEntity reservation) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = reservation.getDate();
        Date dt = sdf.parse(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            receipt.setWeekendDiscount(10);
        } else {
            receipt.setWeekendDiscount(0);
        }
    }

    public ReceiptEntity calculateReceipt(ReservationEntity reservation, ClientEntity client) {
    try {
        ReceiptEntity receipt = new ReceiptEntity();
        Long reservationId = reservation.getId();
        if (reservationId == null) {
            throw new RuntimeException("ID de la reserva es nulo, no se puede asociar un recibo.");
        }

        receipt.setReservationId(reservationId);
        receipt.setClientId(client.getId());
        receipt.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        receipt.setTime(LocalTime.now());
        receipt.setIva((long) (reservation.getPrice() * 0.19));

        // Calcular descuentos y total
        calculateDiscounts(receipt, client, reservation);
        applyDiscount(receipt, reservation);

        return receiptRepository.save(receipt);
    } catch (Exception e) {
        throw new RuntimeException("Error al calcular el recibo: " + e.getMessage(), e);
    }
}

    public void deleteReceipt(Long id) throws Exception{
        receiptRepository.deleteById(id);
    }

    public void deleteReceiptByReservationId(Long reservationId) throws Exception {
        ReceiptEntity receipt = receiptRepository.findByReservationId(reservationId);
        deleteReceipt(receipt.getId());
    }
}