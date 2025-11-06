package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.services.KardexRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/kardexRegisters")
@CrossOrigin("*")
public class KardexRegisterController {

    @Autowired
    KardexRegisterService kardexRegisterService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<KardexRegisterEntity>> listKardexRegisters() {
        List<KardexRegisterEntity> kardex = kardexRegisterService.getKardexRegisters();
        return ResponseEntity.ok(kardex);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<KardexRegisterEntity> getKardexRegisterById(@PathVariable Long id) {
        KardexRegisterEntity register = kardexRegisterService.getKardexRegisterById(id);
        return ResponseEntity.ok(register);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/dateRange")
    public ResponseEntity<List<KardexRegisterEntity>> getKardexRegisterInDateRange(@RequestParam(required = false) String startDate,
                                                                                   @RequestParam(required = false) String endDate){
        List<KardexRegisterEntity> kardexRegisters = kardexRegisterService.getKardexRegisterInDateRange(startDate, endDate);
        return ResponseEntity.ok(kardexRegisters);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/tools/{toolId}")
    public ResponseEntity<List<KardexRegisterEntity>> getKardexRegisterByTool(@PathVariable Long toolId){
        List<KardexRegisterEntity> toolRegisters = kardexRegisterService.getKardexRegisterByToolName(toolId);
        return ResponseEntity.ok(toolRegisters);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteKardexRegister(@PathVariable Long id) throws Exception{
        var isDeleted = kardexRegisterService.deleteKardexRegister(id);
        return ResponseEntity.noContent().build();
    }
}
