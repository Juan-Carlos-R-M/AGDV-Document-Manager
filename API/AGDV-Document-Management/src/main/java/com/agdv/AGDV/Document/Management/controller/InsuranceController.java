package com.agdv.AGDV.Document.Management.controller;

import com.agdv.AGDV.Document.Management.model.Insurance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.agdv.AGDV.Document.Management.service.InsuranceService;

import java.util.List;

@RestController
@RequestMapping("/insurance")
public class InsuranceController {

    @Autowired
    private InsuranceService service;

    /**
     * GET /insurance/getAll
     * Devuelve todas las aseguradoras.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        try {
            List<Insurance> aseguradoras = service.getAllInsurances();
            return ResponseEntity.ok(aseguradoras);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body("{\"error\":\"ocurrió un error al obtener aseguradoras\"}");
        }
    }
}
