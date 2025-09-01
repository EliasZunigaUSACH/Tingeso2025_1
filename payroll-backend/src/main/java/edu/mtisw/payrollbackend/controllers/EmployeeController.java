package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import edu.mtisw.payrollbackend.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kardex")
@CrossOrigin("*")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/")
    public ResponseEntity<List<EmployeeEntity>> listEmployees() {
        List<EmployeeEntity> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/")
    public ResponseEntity<EmployeeEntity> saveEmployee(@RequestBody EmployeeEntity employee){
        EmployeeEntity employeeNew = employeeService.saveEmployee(employee);
        return ResponseEntity.ok(employeeNew);
    }

    @PutMapping("/")
    public ResponseEntity<EmployeeEntity> updateEmployee(@RequestBody EmployeeEntity employee){
        EmployeeEntity employeeUpdated = employeeService.updateEmployee(employee);
        return ResponseEntity.ok(employeeUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteEmployeeById(@PathVariable Long id) throws Exception{
        var isDeleted = employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
