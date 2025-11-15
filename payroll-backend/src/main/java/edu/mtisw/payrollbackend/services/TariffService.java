package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.repositories.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TariffService {

    @Autowired
    TariffRepository tariffRepository;

    public TariffEntity getTariff(){
        return tariffRepository.findById(1L)
                .orElseGet(this::createAndSaveDefaultTariff);
    }

    private TariffEntity createAndSaveDefaultTariff() {
        TariffEntity newTariff = createTariff();
        return tariffRepository.save(newTariff);
    }

    public TariffEntity updateTariff(TariffEntity tariff){
        return tariffRepository.save(tariff);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkTariff(){
        if (tariffRepository.count() == 0){
            TariffEntity tariff = createTariff();
            tariffRepository.save(tariff);
        }
    }

    public TariffEntity createTariff(){
        TariffEntity newTariff = new TariffEntity();
        newTariff.setId(1L);
        newTariff.setDailyTariff(1000L);
        newTariff.setDelayTariff(2000L);
        return newTariff;
    }
}
