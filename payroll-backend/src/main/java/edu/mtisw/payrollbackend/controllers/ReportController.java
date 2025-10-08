package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    ReportService reportService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<ReportEntity>> listReports(){
        List<ReportEntity> reports = reportService.getReports();
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ReportEntity> getReportById(@PathVariable Long id){
        ReportEntity report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/")
    public ResponseEntity<ReportEntity> saveReport(@RequestBody ReportEntity report){
        ReportEntity reportNew = reportService.saveReport(report);
        return ResponseEntity.ok(reportNew);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReportById(@PathVariable Long id) throws Exception{
        var isDeleted = reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{startDate}_to_{endDate}")
    public ResponseEntity<List<ReportEntity>> getReportsByDateRange(@PathVariable String startDate, @PathVariable String endDate){
        List<ReportEntity> reports = reportService.getReportsByDateRange(startDate, endDate);
        return ResponseEntity.ok(reports);
    }
}
