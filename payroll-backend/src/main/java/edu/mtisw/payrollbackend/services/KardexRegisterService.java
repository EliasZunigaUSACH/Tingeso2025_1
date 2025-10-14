package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class KardexRegisterService {
    @Autowired
    KardexRegisterRepository kardexRegisterRepository;

    public List<KardexRegisterEntity> getKardexRegisters(){
        return kardexRegisterRepository.findAll();
    }

    public KardexRegisterEntity getKardexRegisterById(Long id){
        return kardexRegisterRepository.findById(id).get();
    }

    public List<KardexRegisterEntity> getKardexRegisterInDateRange(String startDate, String endDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        return kardexRegisterRepository.findByDateBetween(start, end);
    }

    public List<KardexRegisterEntity> getKardexRegisterByToolName(String toolName){
        return kardexRegisterRepository.findByToolName(toolName);
    }

    public boolean deleteKardexRegister(Long id) throws Exception{
        try{
            kardexRegisterRepository.deleteById(id);
            return true;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
