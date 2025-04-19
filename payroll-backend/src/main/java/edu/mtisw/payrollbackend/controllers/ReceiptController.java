package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import edu.mtisw.payrollbackend.services.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipt")
@CrossOrigin("*")
public class ReceiptController {
    @Autowired
    ReceiptService receiptService;

    @GetMapping("/")
    public ResponseEntity<List<ReceiptEntity>> listReceipts() {
        List<ReceiptEntity> Receipts = receiptService.getReceipts();
        return ResponseEntity.ok(Receipts);
    }
/*
    @GetMapping("/calculate")
    public ResponseEntity<Void> calculatePaychecks(@RequestParam("year") int year, @RequestParam("month") int month) {
        paycheckService.calculatePaychecks(year, month);
        return ResponseEntity.noContent().build();
    }
*/
}