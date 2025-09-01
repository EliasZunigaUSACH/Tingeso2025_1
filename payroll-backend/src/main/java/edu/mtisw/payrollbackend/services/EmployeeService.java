package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import edu.mtisw.payrollbackend.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    public List<EmployeeEntity> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public EmployeeEntity saveEmployee(EmployeeEntity employee){
        return employeeRepository.save(employee);
    }

    public EmployeeEntity updateEmployee(EmployeeEntity employee) {
        return employeeRepository.save(employee);
    }

    public boolean deleteEmployee(Long id) throws Exception {
        try{
            employeeRepository.deleteById(id);
            return true;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public boolean checkEmployeePermission(EmployeeEntity employee){
        return employee.getLevel() == 2;
    }

    public boolean processLoginEmployee(EmployeeEntity employee, String password){
        return employee.getPassword().equals(password);
    }
}
