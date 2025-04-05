package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import org.springframework.stereotype.Service;

import static java.lang.Math.round;

@Service
public class OfficeHRMService {
   // calcula el salario anual del employee
   public int getAnnualSalary(EmployeeEntity employee) {
      int annualSalary = 0;
      if (employee.getSalary()<=30000) {
         if (employee.getSalary() > 0) {
            annualSalary = employee.getSalary() * 12;
         }
      }
      return annualSalary;
   }

   // Calcula bonificacion según monto del sueldo
   // < 2.000: 10% del monto de su sueldo; caso contrario 20%
   public int getSalaryBonus(EmployeeEntity employee) {
      int salaryBonus = 0;
		
      if(employee.getSalary() < 2000){
         salaryBonus = round(employee.getSalary() * 0.1f);
      }else{
         salaryBonus = round(employee.getSalary() * 0.2f);
      }
		
      return salaryBonus;
   }

   // Calcula bonificacion según numero de Hijos
   // < 3: 5% del monto de su sueldo por cada hijo; caso contrario 15%
   public int getChildrenBonus(EmployeeEntity employee) {
      int childrenBonus = 0;

      if(employee.getChildren() < 3){
         childrenBonus = round(employee.getSalary() * 0.05f) * employee.getChildren();
      }else{
         childrenBonus = round(employee.getSalary() * 0.15f) * employee.getChildren();
      }

      return childrenBonus;
   }

   //Calcula bonificacion por numero de horas extras
   //Categoria "A": le corresponde 100 dolares
   //Categoria "B": le corresponde 60 dolares
   //Categoria "C": le corresponde 20 dolares
   public int getExtraHoursBonus(EmployeeEntity employee, int numExtraHours) {
      int extraHoursBonus = 0;

      if(employee.getCategory() == "A"){
         extraHoursBonus = numExtraHours * 100;
      }else{
         if(employee.getCategory() == "B") {
            extraHoursBonus = numExtraHours * 60;
         } else {
            extraHoursBonus = numExtraHours * 20;
         }
      }

      return extraHoursBonus;
   }

}