package com.agdv.AGDV.Document.Management.service;

import com.agdv.AGDV.Document.Management.model.Insurance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.agdv.AGDV.Document.Management.repository.InsuranceRepository;

import java.util.List;

@Service
public class InsuranceService {

    @Autowired
    private InsuranceRepository repository;

    /**
     * Recupera todas las aseguradoras.
     */
    public List<Insurance> getAllInsurers() {
        return repository.findAll();
    }
    public List<Insurance> addInsurers(List<Insurance> insurers) {
        return repository.saveAll(insurers);
    }
}
