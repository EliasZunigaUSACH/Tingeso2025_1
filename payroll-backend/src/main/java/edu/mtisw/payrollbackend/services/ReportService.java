package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ReportRepository;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReportRepository reportRepository;

    public ArrayList<ReportEntity> getReports(){
        return (ArrayList<ReportEntity>) reportRepository.findAll();
    }

    public ReportEntity getReportById(Long id){
        return reportRepository.findById(id).get();
    }

    private List<List<ReservationEntity>> getReservationsOnPeriod(int year, int month, int yearEnd, int monthEnd){
        List<List<ReservationEntity>> reservationsPerMonthList = new ArrayList<>();
        for (int currentYear = year, currentMonth = month;
             currentYear < year + 1 || currentMonth <= monthEnd;
             currentMonth++){
            if (currentMonth > 12){
                currentYear++;
                currentMonth = 1;
            }
            reservationsPerMonthList.add(reservationRepository.getReservationsByYearMonth(String.valueOf(currentYear), String.valueOf(currentMonth)));
        }
        return reservationsPerMonthList;
    }

    private void calculateTotalAmounts(ReportEntity report){
        List<List<ReservationEntity>>reservationsonsPerMonth = report.getReservations();
        List<Long> amounts = new ArrayList<>();
        for (List<ReservationEntity> reservations : reservationsonsPerMonth){
            Long amount = 0L;
            for (ReservationEntity reservation : reservations){
                amount += reservation.getTotal();
            }
            amounts.add(amount);
        }
        report.setAmountPerMonth(amounts);
    }

    public ReportEntity saveReport(ReportEntity report){
        int yearStart, yearEnd, monthStartNum, monthEndNum;
        String start = report.getYearMonthStart();
        String[] startParts = start.split("-");
        yearStart = Integer.parseInt(startParts[0]);
        monthStartNum = Integer.parseInt(startParts[1]);
        String end = report.getYearMonthEnd();
        String[] endParts = end.split("-");
        yearEnd = Integer.parseInt(endParts[0]);
        monthEndNum = Integer.parseInt(endParts[1]);
        calculateTotalAmounts(report);
        List<List<ReservationEntity>> reservationsPerMonth = getReservationsOnPeriod(yearStart, monthStartNum, yearEnd, monthEndNum);
        report.setReservations(reservationsPerMonth);
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
}
