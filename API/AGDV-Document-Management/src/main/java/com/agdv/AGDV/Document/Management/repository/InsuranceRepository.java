package com.agdv.AGDV.Document.Management.repository;

import com.agdv.AGDV.Document.Management.model.Insurance;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InsuranceRepository extends MongoRepository<Insurance, Integer> {
    // Puedes agregar métodos personalizados si necesitas búsquedas más específicas
}
