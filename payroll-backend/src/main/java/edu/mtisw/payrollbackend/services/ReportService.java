package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        LocalDate start, end;
        if (startDate.isEmpty()) start = LocalDate.MIN;
        else start = LocalDate.parse(startDate);

        if (endDate.isEmpty()) end = LocalDate.MAX;
        else end = LocalDate.parse(endDate);

        return reportRepository.findByCreationDateBetween(start.toString(), end.toString());
    }
}
