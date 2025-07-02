package com.agdv.AGDV.Document.Management.controller;

import com.agdv.AGDV.Document.Management.model.Car;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.agdv.AGDV.Document.Management.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarService service;

    private final Gson gson = new Gson();

    @PostMapping("/add")
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        try {
            Car added = service.add(car);
            return ResponseEntity.ok(added);  // Jackson lo convierte a JSON automáticamente
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PatchMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Car> updateCar(@RequestBody Car car) {
        if (car == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            service.update(car);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/delete/{idCar}")
    public ResponseEntity<String> deleteCar(@PathVariable String idCar) {
        if (idCar == null || idCar.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\":\"El ID no puede estar vacío\"}");
        }
        try {
            service.delete(idCar);
            return ResponseEntity.ok("{\"status\":\"success\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error interno del servidor\"}");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<String> getAll() {
        try {
            List<Car> cars = service.getAll();
            return ResponseEntity.ok(gson.toJson(cars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"ocurrió un error al obtener los autos\"}");
        }
    }

    @GetMapping("/searchCar")
    public ResponseEntity<String> searchCar(@RequestParam("query") String query) {
        try {
            List<Car> cars = service.search(query);
            return ResponseEntity.ok(gson.toJson(cars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"ocurrió un error al obtener los autos\"}");
        }
    }
}
