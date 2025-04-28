package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;

    public ArrayList<ReservationEntity> getReservations(){
        return (ArrayList<ReservationEntity>) reservationRepository.findAll();
    }

    public ReservationEntity saveReservation(ReservationEntity reservation){
        Long price = 0L;
        int time = 0, duration = reservation.getTrackTime();
        if (duration <= 10){
            price = 15000L;
            time = 30;
        } else if (10 < duration && duration <= 15) {
            price = 20000L;
            time = 35;
        } else if (15 < duration && duration <= 20) {
            price = 25000L;
            time = 40;
        }
        reservation.setReservationTime(time);
        LocalTime inicio = reservation.getStartTime();
        reservation.setEndTime(inicio.plusMinutes(time));
        reservation.setPrice(price);
        return reservationRepository.save(reservation);
    }

    public ReservationEntity getReservationById(Long id){
        return reservationRepository.findById(id).get();
    }

    public List<ReservationEntity> getReservationByClientId(Long id){
        return (List<ReservationEntity>) reservationRepository.findByClientId(id);
    }

    public ReservationEntity updateReservation(ReservationEntity reservation) {
        int time = 0, duration = reservation.getTrackTime();
        if (duration <= 10){
            time = 30;
        } else if (10 < duration && duration <= 15) {
            time = 35;
        } else if (15 < duration && duration <= 20) {
            time = 40;
        }
        reservation.setReservationTime(time);
        LocalTime inicio = reservation.getStartTime();
        reservation.setEndTime(inicio.plusMinutes(reservation.getReservationTime()));
        return reservationRepository.save(reservation);
    }

    public List<ReservationEntity> getReservationByDate(Date date) {
        return (List<ReservationEntity>) reservationRepository.findByDate(date);
    }
/*
    public int getTotalExtraHoursByRutYearMonth(String rut, int year, int month) {
        List<ReservationEntity> extraHours = reservationRepository.getExtraHoursByRutYearMonth(rut, year, month);
        int sumExtraHours = 0;
        for (ReservationEntity extraHour : extraHours) {
            sumExtraHours = sumExtraHours + extraHour.getNumExtraHours();
        }
        return sumExtraHours;
    }
*/



    public boolean deleteReservation(Long id) throws Exception {
        try{
            reservationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
