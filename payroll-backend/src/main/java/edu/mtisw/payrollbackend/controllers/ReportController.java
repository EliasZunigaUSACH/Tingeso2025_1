package edu.mtisw.payrollbackend.controllers;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.services.ReportService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    ReportService reportService;

    @GetMapping("/")
    public ResponseEntity<List<ReportEntity>> listReports(){
        List<ReportEntity> reports = reportService.getReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportEntity> getReportById(@PathVariable Long id){
        ReportEntity report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/")
    public ResponseEntity<ReportEntity> saveReport(@RequestBody ReportEntity report){
        ReportEntity reportNew = reportService.saveReport(report);
        return ResponseEntity.ok(reportNew);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReportById(@PathVariable Long id) throws Exception{
        var isDeleted = reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
