package com.agdv.AGDV.Document.Management.service;

import com.agdv.AGDV.Document.Management.model.Backup;
import com.agdv.AGDV.Document.Management.config.GridFSConfig;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.agdv.AGDV.Document.Management.repository.BackupRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

@Service
public class BackupService {

    private final BackupRepository backupRepository;
    private final GridFSConfig gridFSConfig;

    @Autowired
    public BackupService(BackupRepository backupRepository, GridFSConfig gridFSConfig) {
        this.backupRepository = backupRepository;
        this.gridFSConfig = gridFSConfig;
    }

    public Backup addBackup(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
        }

        String name = file.getOriginalFilename();
        if (name == null || name.isEmpty()) {
            name = "respaldo-" + System.currentTimeMillis() + ".zip";
        }

        try (InputStream inputStream = file.getInputStream()) {
            ObjectId id = gridFSConfig.gridFSBucket().uploadFromStream(name, inputStream);

            Backup backup = new Backup();
            backup.setId(id.toHexString());
            backup.setName(name);
            backup.setDate(LocalDate.now().toString());
            backup.setSize(String.valueOf(file.getSize()));

            return backupRepository.save(backup);
        } catch (IOException e) {
            throw new RuntimeException("Error al subir el archivo", e);
        }
    }

    public byte[] downloadBackup(String id) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Convertimos el String a ObjectId para la descarga
            gridFSConfig.gridFSBucket().downloadToStream(new ObjectId(id), outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al descargar el archivo", e);
        }
    }

    public List<Backup> getAllBackups() {
        return backupRepository.findAll();
    }
}