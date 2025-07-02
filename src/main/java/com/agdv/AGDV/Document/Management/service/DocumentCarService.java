//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.agdv.AGDV.Document.Management.service;

import com.agdv.AGDV.Document.Management.model.DocumentCar;
import com.agdv.AGDV.Document.Management.repository.DocumentCarRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentCarService {
    @Autowired
    private DocumentCarRepository repository;

    public DocumentCarService() {
    }

    public void addDocument(DocumentCar doc) {
        if (doc.getCarId() != null && !doc.getCarId().isEmpty()) {
            doc.setAsset(1);
            this.repository.save(doc);
        } else {
            throw new IllegalArgumentException("El carId no puede ser nulo o vacío");
        }
    }

    public List<DocumentCar> getDocumentsByCarId(String carId) {
        return this.repository.findByCarIdAndAsset(carId, 1);
    }

    public void deleteDoc(String id) {
        this.repository.findById(id).ifPresent((doc) -> {
            doc.setAsset(0);
            this.repository.save(doc);
        });
    }
}
