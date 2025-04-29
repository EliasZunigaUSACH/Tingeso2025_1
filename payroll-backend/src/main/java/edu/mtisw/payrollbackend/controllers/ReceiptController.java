package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.services.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts")
@CrossOrigin("*")
public class ReceiptController {
    @Autowired
    ReceiptService receiptService;

    @GetMapping("/")
    public ResponseEntity<List<ReceiptEntity>> listReceipts() {
        List<ReceiptEntity> Receipts = receiptService.getReceipts();
        return ResponseEntity.ok(Receipts);
    }

    @PostMapping("/calculate")
    public ResponseEntity<ReceiptEntity> calculateReceipts(@RequestBody ReservationEntity reservation, @RequestBody ClientEntity client) throws IOException, ParseException, InterruptedException {
        ReceiptEntity receiptNew = receiptService.calculateReceipt(reservation, client);
        return ResponseEntity.ok(receiptNew);
    }

}