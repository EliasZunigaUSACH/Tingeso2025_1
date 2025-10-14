package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.services.KardexRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<KardexRegisterEntity> listKardexRegisters() {
        return kardexRegisterService.getKardexRegisters();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public KardexRegisterEntity getKardexRegisterById(@PathVariable Long id) {
        return kardexRegisterService.getKardexRegisterById(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{startDate}_to_{endDate}")
    public List<KardexRegisterEntity> getKardexRegisterInDateRange(@PathVariable String startDate, @PathVariable String endDate){
        return kardexRegisterService.getKardexRegisterInDateRange(startDate, endDate);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/tool/{toolName}")
    public List<KardexRegisterEntity> getKardexRegisterByToolName(@PathVariable String toolName){
        return kardexRegisterService.getKardexRegisterByToolName(toolName);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public boolean deleteKardexRegister(@PathVariable Long id) throws Exception{
        return kardexRegisterService.deleteKardexRegister(id);
    }
}
