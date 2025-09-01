package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.repositories.ReportRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    ReportRepository reportRepository;

    public ArrayList<ReportEntity> getReports(){
        return (ArrayList<ReportEntity>) reportRepository.findAll();
    }

    public ReportEntity getReportById(Long id){
        return reportRepository.findById(id).get();
    }

    public ReportEntity saveReport(ReportEntity report) {
        return reportRepository.save(report);
    }

    public boolean deleteReport(Long id) throws Exception{
        try{
            reportRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<ReportEntity> getReportsByDateRange(LocalDate startDate, LocalDate endDate){
        return reportRepository.findByDateBetween(startDate, endDate);
    }
}
