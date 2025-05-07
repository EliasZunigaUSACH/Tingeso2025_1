package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ReportEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.entities.ReservationGroupEntity;
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

    private List<ReservationGroupEntity> getReservationsOnPeriod(int year, int month, int yearEnd, int monthEnd){
        List<ReservationGroupEntity> reservationsPerMonthList = new ArrayList<>();
        int currentYear = year;
        int currentMonth = month;
        while (currentYear <= yearEnd || currentMonth <= monthEnd){
            ReservationGroupEntity reservationGroup = new ReservationGroupEntity();
            reservationGroup.setReservations(reservationRepository.findByYearMonth(String.valueOf(currentYear), String.valueOf(currentMonth)));
            reservationsPerMonthList.add(reservationGroup);
            currentMonth++;
            if (currentMonth > 12){
                currentMonth = 1;
                currentYear++;
            }
        }
        return reservationsPerMonthList;
    }

    private void calculateTotalAmounts(ReportEntity report){
        List<ReservationGroupEntity> monthReservations = report.getReservationGroups();
        List<Long> amounts = new ArrayList<>();
        for (ReservationGroupEntity group : monthReservations){
            Long amount = 0L;
            for (ReservationEntity reservation : group.getReservations()){
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
        List<ReservationGroupEntity> reservationsPerMonth = getReservationsOnPeriod(yearStart, monthStartNum, yearEnd, monthEndNum);
        report.setReservationGroups(reservationsPerMonth);
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
