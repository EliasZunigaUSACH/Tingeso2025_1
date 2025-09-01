package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.services.KardexRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/kardexRegisters")
@CrossOrigin("*")
public class KardexRegisterController {

    @Autowired
    KardexRegisterService kardexRegisterService;

    @GetMapping("/")
    public List<KardexRegisterEntity> listKardexRegisters() {
        return kardexRegisterService.getKardexRegisters();
    }

    @GetMapping("/{id}")
    public KardexRegisterEntity getKardexRegisterById(@PathVariable Long id) {
        return kardexRegisterService.getKardexRegisterById(id);
    }

    @GetMapping("/range")
    public List<KardexRegisterEntity> getKardexRegisterInDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        return kardexRegisterService.getKardexRegisterInDateRange(startDate, endDate);
    }

    @GetMapping("/tool/{toolName}")
    public List<KardexRegisterEntity> getKardexRegisterByToolName(@PathVariable String toolName){
        return kardexRegisterService.getKardexRegisterByToolName(toolName);
    }

    @PostMapping("/")
    public KardexRegisterEntity saveKardexRegister(@RequestBody KardexRegisterEntity kardexRegister){
        return kardexRegisterService.saveKardexRegister(kardexRegister);
    }

    @DeleteMapping("/{id}")
    public boolean deleteKardexRegister(@PathVariable Long id) throws Exception{
        return kardexRegisterService.deleteKardexRegister(id);
    }
}
