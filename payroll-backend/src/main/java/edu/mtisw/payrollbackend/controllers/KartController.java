package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import edu.mtisw.payrollbackend.repositories.KartRepository;
import edu.mtisw.payrollbackend.services.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.mtisw.payrollbackend.entities.KartEntity;

import java.util.List;

@RestController
@RequestMapping("/api/v1/karts")
@CrossOrigin("*")

public class KartController {

    @Autowired
    KartService kartService;

    @GetMapping("/")
    public ResponseEntity<List<KartEntity>> listKart(){
        List<KartEntity> karts = kartService.getAllKarts();
        return ResponseEntity.ok(karts);
    }

    @GetMapping("{id}")
    public ResponseEntity<KartEntity> getKartById(@PathVariable int id){
        KartEntity kart = kartService.getKartById(id);
        return ResponseEntity.ok(kart);
    }

    @GetMapping("/")
    public ResponseEntity<KartEntity> saveKart(@RequestBody KartEntity kart){
        KartEntity kartNew = kartService.saveKart(kart);
        return ResponseEntity.ok(kartNew);
    }
/*
    @GetMapping("/")
    public ResponseEntity<KartEntity> updateKart(@RequestBody KartEntity kart){
        KartEntity kartUpdated = kartService.updateKart(kart);
        return ResponseEntity.ok(kartUpdated);
    }
*/
}
