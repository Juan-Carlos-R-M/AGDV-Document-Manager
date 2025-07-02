//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.agdv.AGDV.Document.Management.repository;

import com.agdv.AGDV.Document.Management.model.DocumentCar;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentCarRepository extends MongoRepository<DocumentCar, String> {
    List<DocumentCar> findByCarIdAndAsset(String carId, int asset);
}
