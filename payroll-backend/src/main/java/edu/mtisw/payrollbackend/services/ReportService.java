package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.LoanEntity;
import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.entities.ToolEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.ReportRepository;
import edu.mtisw.payrollbackend.repositories.LoanRepository;
import edu.mtisw.payrollbackend.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    ClientService clientService;

    @Autowired
    ToolService toolService;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    LoanService loanService;

    @Autowired
    LoanRepository loanRepository;

    public ArrayList<ReportEntity> getReports(){
        return (ArrayList<ReportEntity>) reportRepository.findAll();
    }

    public ReportEntity getReportById(Long id){
        return reportRepository.findById(id).get();
    }

    public ReportEntity saveReport(ReportEntity report) {
        report.setActiveLoans(loanService.getActiveDelayedLoansData(false));
        report.setDelayedLoans(loanService.getActiveDelayedLoansData(true));
        report.setClientsWithDelayedLoans(clientService.getClientsWithDelayedLoans());
        report.setTopTools(toolService.getTop10Tools());
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

    public List<ReportEntity> getReportsByDateRange(String startDate, String endDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        return reportRepository.findByDateBetween(start, end);
    }
}
