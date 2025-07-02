//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.agdv.AGDV.Document.Management.controller;

import com.agdv.AGDV.Document.Management.model.DocumentCar;
import com.agdv.AGDV.Document.Management.service.DocumentCarService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/document"})
public class DocumentCarController {
    @Autowired
    private DocumentCarService service;

    @PostMapping("/add")
    public ResponseEntity<?> addDocument(
            @RequestParam("idCar") String carId,
            @RequestBody DocumentCar document
    ) {
        try {
            document.setCarId(carId);
            service.addDocument(document);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"ocurrió un error\"}");
        }
    }


    @GetMapping({"/byCar"})
    public ResponseEntity<?> getByCarId(@RequestParam("idCar") String carId) {
        try {
            List<DocumentCar> docs = this.service.getDocumentsByCarId(carId);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Ocurrió un error al obtener los documentos\"}");
        }
    }

    @DeleteMapping({"/delete/{documentId}"})
    public ResponseEntity<String> delete(@PathVariable String documentId) {
        try {
            this.service.deleteDoc(documentId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error");
        }
    }
}
