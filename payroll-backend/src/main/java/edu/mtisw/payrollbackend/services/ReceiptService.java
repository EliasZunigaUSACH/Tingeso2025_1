package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.ClientEntity;
import edu.mtisw.payrollbackend.entities.ReceiptEntity;
import edu.mtisw.payrollbackend.entities.ReservationEntity;
import edu.mtisw.payrollbackend.repositories.ReceiptRepository;
import edu.mtisw.payrollbackend.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class ReceiptService {
    @Autowired
    ReceiptRepository receiptRepository;
    @Autowired
    ClientService clientService;
    @Autowired
    OfficeHRMService officeHRMService;

    @Autowired
    ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;

    public ArrayList<ReceiptEntity> getReceipts(){
        return (ArrayList<ReceiptEntity>) receiptRepository.findAll();
    }

    private void applyDiscount(ReceiptEntity receipt, ClientEntity client, Long price) {
        Long total;
        double priceRatio = 1.0;
        switch (client.getFrequencyLevelClient()){
            case 1: priceRatio -= 0.0;
            case 2: priceRatio -= 0.1;
            case 3: priceRatio -= 0.2;
            case 4: priceRatio -= 0.3;
        }
        if () {

        }
        if (priceRatio < 0.5){
            total = Math.round(price * priceRatio);
            receipt.setTotal(total);
            return;
        }


        total = Math.round(price * priceRatio);
    }

    public ReceiptEntity makeReceipt(Long reservationId, Long clientId) {
        Optional<ReservationEntity> reservation = reservationRepository.findById(reservationId);
        if (reservation.isEmpty()) {
            throw new IllegalArgumentException("Reserva no encontrada");
        }
        ReceiptEntity receipt = new ReceiptEntity();
        Date now = new Date();
        receipt.setDate(now);
        receipt.setReservationId(reservationId);
        receipt.setClientId(clientId);

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
