package com.agdv.AGDV.Document.Management.repository;


import com.agdv.AGDV.Document.Management.model.Backup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BackupRepository extends MongoRepository<Backup, String> {
    // Puedes agregar métodos personalizados si los necesitas, por ejemplo:

    // Buscar respaldos por fecha
    // List<Backup> findByDate(String date);

    // Eliminar por ID (ya está incluido en MongoRepository)
}