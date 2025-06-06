package com.agdv.AGDV.Document.Management.service;

import com.agdv.AGDV.Document.Management.model.DocumentCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.agdv.AGDV.Document.Management.repository.DocumentCarRepository;

import java.util.List;

@Service
public class DocumentCarService {

    @Autowired
    private DocumentCarRepository repository;

    public void addDocument(DocumentCar doc) {
        if (doc.getCarId() == null || doc.getCarId().isEmpty()) {
            throw new IllegalArgumentException("El carId no puede ser nulo o vacío");
        }
        doc.setAsset(1); // activo por defecto
        repository.save(doc);
    }

    public List<DocumentCar> getDocumentsByCarId(String carId) {
        return repository.findByCarIdAndAsset(carId, 1);
    }

    public void deleteDoc(String id) {
        repository.findById(id).ifPresent(doc -> {
            doc.setAsset(0);
            repository.save(doc);
        });
    }
}
