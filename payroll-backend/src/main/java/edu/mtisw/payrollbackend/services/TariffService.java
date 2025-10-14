package edu.mtisw.payrollbackend.services;

import edu.mtisw.payrollbackend.entities.TariffEntity;
import edu.mtisw.payrollbackend.repositories.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TariffService {

    @Autowired
    TariffRepository tariffRepository;

    public TariffEntity getTariff(){
        // Garantizar que retornamos el único registro disponible (ID = 1L)
        return tariffRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Tariff not found. Please initialize the tariff."));
    }

    public TariffEntity updateTariff(TariffEntity tariff){
        // Reforzar que el ID siempre es 1L para controlar la unicidad
        tariff.setId(1L);
        return tariffRepository.save(tariff);
    }

    public TariffEntity initializeTariff(TariffEntity tariff) {
        // Garantizar que solo se inicializa si no existe un registro con ID = 1L
        if (tariffRepository.existsById(1L)) {
            throw new IllegalStateException("Tariff is already initialized.");
        }
        tariff.setId(1L);
        return tariffRepository.save(tariff);
    }
}
