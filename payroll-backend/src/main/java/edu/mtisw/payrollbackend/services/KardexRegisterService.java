package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.*;
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
        LocalDate start, end;
        if (startDate.isEmpty()) start = LocalDate.of(0, Month.JANUARY, 1);
        else start = LocalDate.parse(startDate);

        if (endDate.isEmpty()) end = LocalDate.of(9999, Month.DECEMBER, 31);
        else end = LocalDate.parse(endDate);

        return kardexRegisterRepository.findByDateBetween(start, end);
    }

    public List<KardexRegisterEntity> getKardexRegisterByToolName(Long toolId){
        return kardexRegisterRepository.findByToolId(toolId);
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
