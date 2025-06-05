package com.agdv.AGDV.Document.Management.controller;

import com.agdv.AGDV.Document.Management.model.DocumentCar;
import com.agdv.AGDV.Document.Management.service.DocumentCarService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentCarController {

    @Autowired
    private DocumentCarService service;

    /**
     * Agregar documento mediante application/x-www-form-urlencoded
     * Parámetros: carId, docData (JSON)
     */
    @PostMapping(value = "/add",  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> addDocument(@RequestParam("idCar") String carId, @RequestParam("docData") String docData) {
        Gson gson = new Gson();
        try {
            DocumentCar doc = gson.fromJson(docData, DocumentCar.class);
            doc.setCarId(carId);
            service.addDocument(doc);
            return ResponseEntity.ok(gson.toJson(doc));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"error\":\"formato de datos no válido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"ocurrió un error\"}");
        }
    }

    /**
     * Obtener documentos por ID de auto (carId)
     * Ejemplo: GET /documents/byCar?carId=123
     */
    @GetMapping("/byCar")
    public ResponseEntity<?> getByCarId(@RequestParam("carId") String carId) {
        try {
            List<DocumentCar> docs = service.getDocumentsByCarId(carId);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Ocurrió un error al obtener los documentos\"}");
        }
    }

    /**
     * Eliminar documento por ID
     * Ejemplo: DELETE /documents/delete/abc123
     */
    @DeleteMapping("/delete/{documentId}")
    public ResponseEntity<String> delete(@PathVariable String documentId) {
        try {
            service.deleteDoc(documentId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error");
        }
    }
}
