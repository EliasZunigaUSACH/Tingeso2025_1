package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.repositories.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class TariffService {

    @Autowired
    TariffRepository tariffRepository;

    public TariffEntity getTariff(){
        return tariffRepository.findById(1L).get();
    }

    public TariffEntity updateTariff(TariffEntity tariff){
        return tariffRepository.save(tariff);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkTariff(){
        if (tariffRepository.count() == 0){
            TariffEntity tariff = new TariffEntity();
            tariff.setId(1L);
            tariff.setDailyTariff(1000L);
            tariff.setDelayTariff(2000L);
            tariffRepository.save(tariff);
        }
    }
}
