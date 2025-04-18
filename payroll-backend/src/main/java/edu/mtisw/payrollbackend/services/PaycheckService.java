package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.PaycheckEntity;
import edu.mtisw.payrollbackend.repositories.PaycheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaycheckService {
    @Autowired
    PaycheckRepository paycheckRepository;
    @Autowired
    ClientService clientService;
    @Autowired
    OfficeHRMService officeHRMService;

    @Autowired
    ReservationService reservationService;
    public ArrayList<PaycheckEntity> getPaychecks(){
        return (ArrayList<PaycheckEntity>) paycheckRepository.findAll();
    }

    public PaycheckEntity savePaycheck(PaycheckEntity paycheck){
        return paycheckRepository.save(paycheck);
    }

    public ArrayList<PaycheckEntity> getPaychecksByYearMonth(Integer year, Integer month){
        return (ArrayList<PaycheckEntity>) paycheckRepository.getPaychecksByYearMonth(year,month);
    }
/*
    public Boolean calculatePaychecks(int year, int month){
        List<ClientEntity> employees = clientService.getEmployees();

        for (ClientEntity employee : employees) {
            PaycheckEntity paycheck = new PaycheckEntity();
            paycheck.setRut(employee.getRut());
            paycheck.setYear(year);
            paycheck.setMonth(month);
            paycheck.setMonthlySalary(employee.getSalary());

            int salaryBonus = officeHRMService.getSalaryBonus(employee);
            paycheck.setSalaryBonus(salaryBonus);

            int childrenBonus = officeHRMService.getChildrenBonus(employee);
            paycheck.setChildrenBonus(childrenBonus);

            int numExtraHours = reservationService.getTotalExtraHoursByRutYearMonth(employee.getRut(), year, month);
            int extraHoursBonus = officeHRMService.getExtraHoursBonus(employee,numExtraHours);
            paycheck.setExtraHoursBonus(extraHoursBonus);

            paycheck.setTotalSalary(employee.getSalary() + salaryBonus + childrenBonus + extraHoursBonus);

            paycheckRepository.save(paycheck);
        }

        return true;
    }
*/
}
