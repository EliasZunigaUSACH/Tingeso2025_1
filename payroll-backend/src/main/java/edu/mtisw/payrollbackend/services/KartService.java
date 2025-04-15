package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.KartEntity;
import edu.mtisw.payrollbackend.repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KartService {
     @Autowired
     KartRepository kartRepository;

     public KartEntity saveKart(KartEntity kart) {
          return kartRepository.save(kart);
     }

     public List<KartEntity> getAllKarts(){
          return kartRepository.findAll();
     }

     public KartEntity getKartById(int idNum){
          String id;
          if (idNum < 10){
               id = "K00" + idNum;
          } else {
               id = "K0" + idNum;
          }
          /*
          return kartRepository.findById(id)
                  .orElseThrow(() -> new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Kart not found with id " + id
                  ));

           */
          return kartRepository.findById(id);
     }

/*
     public List<String> getAllKartIds(){
          return KartRepository.findAll()
                  .stream()
                  .map()
                  .collect(Collectors.toList());
     }
*/
     public KartEntity updateKart(KartEntity kart) {
          return kartRepository.save(kart);
     }
}
