package com.agdv.AGDV.Document.Management.controller;

import com.agdv.AGDV.Document.Management.model.Backup;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.agdv.AGDV.Document.Management.service.BackupService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/backup") // ← ruta base
public class BackupController {

    @Autowired
    private BackupService service;

    @GetMapping("/getAll")
    public List<Backup> getAllBackups() {
        return service.getAllBackups();
    }

    @PostMapping("/add")
    public ResponseEntity<String> loadFile(@RequestParam("file") MultipartFile file) {
        Backup backup = service.addBackup(file);
        return ResponseEntity.ok("todo bien" + backup.toString());
    }

    // BackupController.java
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadBackup(@PathVariable String id) {
        try {
            byte[] fileData = service.downloadBackup(id);
            if (fileData == null || fileData.length == 0) {
                return ResponseEntity.status(404).build();
            }
            return ResponseEntity.ok()
                    .header("Content-Type", "application/zip")
                    .header("Content-Disposition", "attachment; filename=respaldo-" + id + ".zip")
                    .body(fileData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}