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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

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

    private void applyDiscount(ReceiptEntity receipt, Optional<ReservationEntity> reservation) {
        Long precio = reservation.get().getPrice();
        int totalDiscount = receipt.getPeopleQuantityDiscount()
                            + receipt.getFidelityDiscount()
                            + receipt.getSpecialDayDiscount()
                            + receipt.getWeekendDiscount()
                            + receipt.getBirthdayDiscount();
        if (totalDiscount >= 100) {
            receipt.setTotal(0L);
        } else {
            Long descuento = precio * totalDiscount / 100;
            Long total = precio - descuento + receipt.getIva();
            receipt.setTotal(total);
        }
    }

    private void calculateDiscounts(ReceiptEntity receipt, Optional<ClientEntity> client, Optional<ReservationEntity> reservation) throws IOException, ParseException, InterruptedException {
        //Aplicación descuento por fidelidad del cliente
        switch (client.get().getFidelityLevel()){
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
        int quantity = reservation.get().getPeopleQuantity();
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String reservationDate = sdf.format(receipt.getDate());
        if (reservationDate.equals(client.get().getBirthday())){
            receipt.setBirthdayDiscount(50);
        } else {
            receipt.setBirthdayDiscount(0);
        }
    }

    private List<Date> obtainSpecialDays() throws IOException, InterruptedException, ParseException {
        List<Date> feriados = new ArrayList<>();
        String url = "https://apis.digital.gob.cl/fl/feriados";
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONArray jsonArray = new JSONArray(response.body());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String fechaStr = obj.getString("fecha");
            Date fecha = sdf.parse(fechaStr);
            feriados.add(fecha);
        }

        return feriados;
    }

    private void calcSpecialDayDiscount(ReceiptEntity receipt, Optional<ReservationEntity> reservation) throws IOException, ParseException, InterruptedException {
        Date date = reservation.get().getDate();
        List<Date> specialDays = obtainSpecialDays();
        if (specialDays.contains(date)){
            receipt.setSpecialDayDiscount(15);
        } else {
            receipt.setSpecialDayDiscount(0);
        }
    }

    private void calcWeekendDiscount(ReceiptEntity receipt, Optional<ReservationEntity> reservation) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(receipt.getDate());
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            receipt.setWeekendDiscount(10);
        } else {
            receipt.setWeekendDiscount(0);
        }
    }

    public ReceiptEntity makeReceipt(Long reservationId, Long clientId) throws IOException, ParseException, InterruptedException {
        Optional<ReservationEntity> reservation = reservationRepository.findById(reservationId);
        if (reservation.isEmpty()) {
            throw new IllegalArgumentException("Reserva no encontrada");
        }
        Optional<ClientEntity> client = clientRepository.findById(clientId);
        ReceiptEntity receipt = new ReceiptEntity();
        Date now = new Date();
        LocalTime nowTime = LocalTime.now();
        receipt.setDate(now);
        receipt.setTime(nowTime);
        receipt.setReservationId(reservationId);
        receipt.setClientId(clientId);
        Long IVA = (long) (reservation.get().getPrice() * 0.19);
        receipt.setIva(IVA);
        calculateDiscounts(receipt, client, reservation);
        applyDiscount(receipt, reservation);
        return receiptRepository.save(receipt);
    }

    public boolean deleteReceipt(Long id) throws Exception{
        try{
            ReceiptEntity receipt = receiptRepository.findById(id).get();
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
