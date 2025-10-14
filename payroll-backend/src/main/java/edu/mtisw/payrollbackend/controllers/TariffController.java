package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.services.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/tariff")
@CrossOrigin("*")
public class TariffController {

    @Autowired
    TariffService tariffService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/")
    public ResponseEntity<TariffEntity>  getTariff(){
        TariffEntity tariff = tariffService.getTariff();
        return ResponseEntity.ok(tariff);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/")
    public ResponseEntity<TariffEntity> updateTariff(@RequestBody TariffEntity tariff){
        TariffEntity updatedTariff = tariffService.updateTariff(tariff);
        return ResponseEntity.ok(updatedTariff);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<TariffEntity> initializeTariff(@RequestBody TariffEntity tariff) {
        TariffEntity initializedTariff = tariffService.initializeTariff(tariff);
        return ResponseEntity.ok(initializedTariff);
    }
}
