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

    public EmployeeEntity getEmployeeById(Long id){
        return employeeRepository.findById(id).get();
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

    public boolean authorizeAdminPermission(EmployeeEntity employee){
        return employee.getLevel() == 2;
    }

    public boolean checkLoginEmployee(String email, String password){
        EmployeeEntity user = employeeRepository.findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }
}
