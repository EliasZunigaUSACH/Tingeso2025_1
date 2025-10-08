package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import edu.mtisw.payrollbackend.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@CrossOrigin("*")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<EmployeeEntity>> listEmployees() {
        List<EmployeeEntity> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<EmployeeEntity> saveEmployee(@RequestBody EmployeeEntity employee){
        EmployeeEntity employeeNew = employeeService.saveEmployee(employee);
        return ResponseEntity.ok(employeeNew);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteEmployeeById(@PathVariable Long id) throws Exception{
        var isDeleted = employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
