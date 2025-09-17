package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.KardexRegisterEntity;
import edu.mtisw.payrollbackend.repositories.KardexRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public KardexRegisterEntity saveKardexRegister(KardexRegisterEntity kardexRegister){
        if (kardexRegister.getToolId() == 1){
            kardexRegister.setClientId(null);
            kardexRegister.setClientName("No aplica");
            kardexRegister.setLoanId(null);
        }
        return kardexRegisterRepository.save(kardexRegister);
    }

    public List<KardexRegisterEntity> getKardexRegisterInDateRange(LocalDate startDate, LocalDate endDate){
        return kardexRegisterRepository.findByDateBetween(startDate, endDate);
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

    public List<KardexRegisterEntity> getLoansRegisters(){
        return kardexRegisterRepository.findByTypeRelated(2);
    }

    public List<KardexRegisterEntity> getToolsRegisters(){
        return kardexRegisterRepository.findByTypeRelated(1);
    }
}
