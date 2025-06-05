package com.agdv.AGDV.Document.Management.repository;

import com.agdv.AGDV.Document.Management.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CarRepository extends MongoRepository<Car, String> {
    List<Car> findByAsset(int asset);
    @Query("{ '$and': [ " +
            "  { 'asset': ?5 }, " +
            "  { '$or': [ " +
            "    { 'brand': { $regex: ?0, $options: 'i' } }, " +
            "    { 'model': { $regex: ?1, $options: 'i' } }, " +
            "    { 'color': { $regex: ?2, $options: 'i' } }, " +
            "    { 'observations': { $regex: ?3, $options: 'i' } }, " +
            "    { '_id': { $regex: ?4, $options: 'i' } } " +
            "  ]} " +
            "]}")
    List<Car> searchByMultipleFields(String brand, String model, String color, String observations, String id, int asset);
}
