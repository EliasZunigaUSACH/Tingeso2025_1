package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@CrossOrigin("*")
public class LoanController {

    @Autowired
    LoanService loanService;

    @GetMapping("/")
    public ResponseEntity<List<LoanEntity>> listLoans() {
        List<LoanEntity> loans = loanService.getLoans();
        return ResponseEntity.ok(loans);

    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanEntity> getLoan(@PathVariable Long id) {
        LoanEntity loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @PostMapping("/")
    public ResponseEntity<LoanEntity> saveLoan(@RequestBody LoanEntity loan, @RequestParam Long clientId) {
        LoanEntity loanNew = loanService.saveLoan(loan, clientId);
        return ResponseEntity.ok(loanNew);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteLoanById(@PathVariable Long id) throws Exception {
        var isDeleted = loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}