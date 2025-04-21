package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ClientRepository;
import edu.mtisw.payrollbackend.repositories.ReceiptRepository;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class ReceiptService {
    @Autowired
    ReceiptRepository receiptRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ClientRepository clientRepository;

    public ArrayList<ReceiptEntity> getReceipts(){
        return (ArrayList<ReceiptEntity>) receiptRepository.findAll();
    }

    private void applyDiscount(ReceiptEntity receipt, Optional<ClientEntity> client, Optional<ReservationEntity> reservation) {
        Long total, price;
        price = reservation.get().getPrice();
        double priceRatio = 1.0;
        applyFidelityDiscount(client, priceRatio);
        applyPeopleQuantityDiscount(reservation.get().getPeopleQuantity(), priceRatio);
        if (priceRatio <= 0.5){
            total = Math.round(price * 0.5);
            receipt.setTotal(total);
            return;
        }


        total = Math.round(price * priceRatio);
        reservation.get().setPrice(total);
    }

    private void applyFidelityDiscount(Optional<ClientEntity> client, double priceRatio) {
        switch (client.get().getFidelityLevel()){
            case 1: priceRatio -= 0.0;
            case 2: priceRatio -= 0.1;
            case 3: priceRatio -= 0.2;
            case 4: priceRatio -= 0.3;
        }
    }

    private void applyPeopleQuantityDiscount(int quantity, double priceRatio){
        if (3 <= quantity && quantity <= 5) {
            priceRatio -= 0.1;
        } else if (6 <= quantity && quantity <= 10) {
            priceRatio -= 0.2;
        } else if (11 <= quantity && quantity <= 15) {
            priceRatio -= 0.3;
        }
    }

    private void applySpecialDayDiscount(Optional<ReservationEntity> reservation, Optional<ClientEntity> client, Long priceRatio) {

    }

    public ReceiptEntity makeReceipt(Long reservationId, Long clientId) {
        Optional<ReservationEntity> reservation = reservationRepository.findById(reservationId);
        if (reservation.isEmpty()) {
            throw new IllegalArgumentException("Reserva no encontrada");
        }
        Optional<ClientEntity> client = clientRepository.findById(clientId);
        ReceiptEntity receipt = new ReceiptEntity();
        Date now = new Date();
        receipt.setDate(now);
        receipt.setReservationId(reservationId);
        receipt.setClientId(clientId);
        applyDiscount(receipt, client, reservation);
        return receiptRepository.save(receipt);
    }
/*
    public Boolean calculatePaychecks(int year, int month){
        List<ClientEntity> employees = clientService.getEmployees();

        for (ClientEntity employee : employees) {
            PaycheckEntity paycheck = new PaycheckEntity();
            paycheck.setRut(employee.getRut());
            paycheck.setYear(year);
            paycheck.setMonth(month);
            paycheck.setMonthlySalary(employee.getSalary());

            int salaryBonus = officeHRMService.getSalaryBonus(employee);
            paycheck.setSalaryBonus(salaryBonus);

            int childrenBonus = officeHRMService.getChildrenBonus(employee);
            paycheck.setChildrenBonus(childrenBonus);

            int numExtraHours = reservationService.getTotalExtraHoursByRutYearMonth(employee.getRut(), year, month);
            int extraHoursBonus = officeHRMService.getExtraHoursBonus(employee,numExtraHours);
            paycheck.setExtraHoursBonus(extraHoursBonus);

            paycheck.setTotalSalary(employee.getSalary() + salaryBonus + childrenBonus + extraHoursBonus);

            paycheckRepository.save(paycheck);
        }

        return true;
    }
*/

    public boolean deleteReceipt(Long id) throws Exception{
        try{
            ReceiptEntity receipt = receiptRepository.findById(id).get();
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
