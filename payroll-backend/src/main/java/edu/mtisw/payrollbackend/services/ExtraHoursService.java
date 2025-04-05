package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ExtraHoursEntity;
import edu.mtisw.payrollbackend.repositories.ExtraHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExtraHoursService {
    @Autowired
    ExtraHoursRepository extraHoursRepository;

    public ArrayList<ExtraHoursEntity> getExtraHours(){
        return (ArrayList<ExtraHoursEntity>) extraHoursRepository.findAll();
    }

    public ExtraHoursEntity saveExtraHours(ExtraHoursEntity extraHour){
        return extraHoursRepository.save(extraHour);
    }

    public ExtraHoursEntity getExtraHourById(Long id){
        return extraHoursRepository.findById(id).get();
    }

    public List<ExtraHoursEntity> getExtraHourByRut(String rut){
        return (List<ExtraHoursEntity>) extraHoursRepository.findByRut(rut);
    }

    public ExtraHoursEntity updateExtraHour(ExtraHoursEntity extraHour) {
        return extraHoursRepository.save(extraHour);
    }

    public List<ExtraHoursEntity> getExtraHoursByRutYearMonth(String rut, int year, int month) {
        return (List<ExtraHoursEntity>) extraHoursRepository.getExtraHoursByRutYearMonth(rut, year, month);
    }

    public int getTotalExtraHoursByRutYearMonth(String rut, int year, int month) {
        List<ExtraHoursEntity> extraHours = extraHoursRepository.getExtraHoursByRutYearMonth(rut, year, month);
        int sumExtraHours = 0;
        for (ExtraHoursEntity extraHour : extraHours) {
            sumExtraHours = sumExtraHours + extraHour.getNumExtraHours();
        }
        return sumExtraHours;
    }

    public boolean deleteExtraHour(Long id) throws Exception {
        try{
            extraHoursRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
