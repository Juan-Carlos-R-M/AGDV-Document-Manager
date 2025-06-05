package com.agdv.AGDV.Document.Management.service;

import com.agdv.AGDV.Document.Management.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.agdv.AGDV.Document.Management.repository.CarRepository;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository repository;

    public Car add(Car car) {
        car.setAsset(1); // Activo por defecto
        return repository.save(car);
    }

    public void update(Car car) {
        repository.save(car); // Save hace upsert (actualiza si existe, crea si no)
    }

    public void delete(String id) {
        repository.findById(id).ifPresent(car -> {
            car.setAsset(0); // Desactivado
            repository.save(car);
        });
    }

    public List<Car> getAll() {
        return repository.findByAsset(1); // Solo activos
    }

    public List<Car> search(String query) {
        try {
            return repository.searchByMultipleFields(query, query, query, query, query, 1);
        } catch (Exception e) {
            System.err.println("Error al buscar autos: " + e.getMessage());
            return List.of();
        }
    }
}
