package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.ReceiptRepository;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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