package com.agdv.AGDV.Document.Management.repository;


import com.agdv.AGDV.Document.Management.model.Backup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BackupRepository extends MongoRepository<Backup, String> {

}