package com.agdv.AGDV.Document.Management.controller;

import com.agdv.AGDV.Document.Management.model.Insurance;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.agdv.AGDV.Document.Management.service.InsuranceService;

import java.io.File;
import java.io.InputStream;
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
            List<Insurance> insurers = service.getAllInsurers();
            if (insurers.isEmpty()){
                ObjectMapper objectMapper = new ObjectMapper();
                InputStream inputStream = getClass().getResourceAsStream("/json/agdv.insurance.json");
                List<Insurance> insurersJson = objectMapper.readValue(inputStream, new TypeReference<List<Insurance>>() {});
                service.addInsurers(insurersJson);
                getAll();
            }
            return ResponseEntity.ok(insurers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body("{\"error\":\"ocurrió un error al obtener aseguradoras\"}");
        }
    }


}
