package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OfficeHRMServiceTest {

    OfficeHRMService officeHRM = new OfficeHRMService();
    EmployeeEntity employee = new EmployeeEntity();

    @Test
    void whenGetAnnualSalary_thenAnualSalary() {
        //Given
        employee.setRut("13.777.548-2");
        employee.setName("Carlos");
        employee.setChildren(3);
        employee.setSalary(2000);
        employee.setCategory("B");

        //When
        int anualSalary = officeHRM.getAnnualSalary(employee);

        //Then
        assertThat(anualSalary).isEqualTo(24000);
    }

    @Test
    void whenGetAnnualSalaryNegative_thenAnualSalary() {
        //Given
        employee.setRut("13.777.548-2");
        employee.setName("Carlos");
        employee.setChildren(3);
        employee.setSalary(-2000);
        employee.setCategory("B");

        //When
        int anualSalary = officeHRM.getAnnualSalary(employee);

        //Then
        assertThat(anualSalary).isEqualTo(0);
    }

    @Test
    void whenGetAnnualSalaryBIG_thenAnualSalary() {
        //Given
        employee.setRut("13.777.548-2");
        employee.setName("Carlos");
        employee.setChildren(3);
        employee.setSalary(50000);
        employee.setCategory("B");

        //When
        int anualSalary = officeHRM.getAnnualSalary(employee);

        //Then
        assertThat(anualSalary).isEqualTo(0);
    }


    @Test
    void whenSalaryLessThan2000_thenSalaryBonusIs10Percent() {
        //Given
        employee.setRut("13.777.678-2");
        employee.setName("Felipe");
        employee.setChildren(2);
        employee.setSalary(1500);
        employee.setCategory("A");

        //When
        int salaryBonus = officeHRM.getSalaryBonus(employee);

        //Then
        assertThat(salaryBonus).isEqualTo(150);
    }

    @Test
    void whenSalaryGreaterThanOrEqualTo2000_thenSalaryBonusIs20Percent() {
        //Given
        employee.setRut("9.698.542-2");
        employee.setName("Maria");
        employee.setChildren(1);
        employee.setSalary(2500);
        employee.setCategory("B");

        //When
        int salaryBonus = officeHRM.getSalaryBonus(employee);

        //Then
        assertThat(salaryBonus).isEqualTo(500);
    }

    @Test
    void whenChildrenLessThan3_thenChildrenBonusIs5PercentPerChild() {
        //Given
        employee.setRut("11.456.765-3");
        employee.setName("Jorge");
        employee.setChildren(2);
        employee.setSalary(2000);
        employee.setCategory("A");

        //When
        int childrenBonus = officeHRM.getChildrenBonus(employee);

        //Then
        assertThat(childrenBonus).isEqualTo(200);
    }

    @Test
    void whenChildrenGreaterThanOrEqualTo3_thenChildrenBonusIs15Percent() {
        //Given
        employee.setRut("10.410.512-3");
        employee.setName("Alfredo");
        employee.setChildren(3);
        employee.setSalary(2000);
        employee.setCategory("B");

        //When
        int childrenBonus = officeHRM.getChildrenBonus(employee);

        //Then
        assertThat(childrenBonus).isEqualTo(900);
    }

    @Test
    void whenCategoryA_thenExtraHoursBonusIs100PerHour() {
        //Given
        employee.setRut("12.654.872-3");
        employee.setName("Andres");
        employee.setChildren(2);
        employee.setSalary(2000);
        employee.setCategory("A");

        //When
        int bonus = officeHRM.getExtraHoursBonus(employee, 3);

        //Then
        assertThat(bonus).isEqualTo(300);
    }

    @Test
    void whenCategoryB_thenExtraHoursBonusIs60PerHour() {
        //Given
        employee.setRut("8.325.284-7");
        employee.setName("Jorge");
        employee.setChildren(1);
        employee.setSalary(2500);
        employee.setCategory("B");

        //When
        int bonus = officeHRM.getExtraHoursBonus(employee, 2);

        //Then
        assertThat(bonus).isEqualTo(120);
    }


}