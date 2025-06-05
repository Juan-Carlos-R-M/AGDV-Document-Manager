package com.agdv.AGDV.Document.Management.repository;

import com.agdv.AGDV.Document.Management.model.DocumentCar;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DocumentCarRepository extends MongoRepository<DocumentCar, String> {
    List<DocumentCar> findByCarIdAndAsset(String carId, int asset);
}
