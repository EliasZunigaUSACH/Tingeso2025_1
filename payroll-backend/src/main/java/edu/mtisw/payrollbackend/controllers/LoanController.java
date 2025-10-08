package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@CrossOrigin("*")
public class LoanController {

    @Autowired
    LoanService loanService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<LoanEntity>> listLoans() {
        List<LoanEntity> loans = loanService.getLoans();
        return ResponseEntity.ok(loans);

    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<LoanEntity> getLoan(@PathVariable Long id) {
        LoanEntity loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/")
    public ResponseEntity<LoanEntity> saveLoan(@RequestBody LoanEntity loan) {
        LoanEntity loanNew = loanService.saveLoan(loan);
        return ResponseEntity.ok(loanNew);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/")
    public ResponseEntity<LoanEntity> updateLoan(@RequestBody LoanEntity loan) {
        LoanEntity loanUpdated = loanService.updateLoan(loan);
        return ResponseEntity.ok(loanUpdated);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteLoanById(@PathVariable Long id) throws Exception {
        var isDeleted = loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}