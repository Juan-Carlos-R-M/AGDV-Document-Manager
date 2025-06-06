package com.agdv.AGDV.Document.Management.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "car")

public class Car {
    @Id
    private String id; // Ejemplo: "7887GUEU788HU"
    private String brand; // Marca
    private String model;
    private int year;
    private String color;
    private String observations;
    private Insurance insurance; // FK hacia Insurance
    private String image; // ID de la imagen
    private int asset;

    public Car() {}

    public Car(String id, String brand, String model, int year, String color, String observations, Insurance insurance, String image, int asset) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.observations = observations;
        this.insurance = insurance;
        this.image = image;
        this.asset = asset;
    }

    public int getAsset() {
        return asset;
    }

    public void setAsset(int asset) {
        this.asset = asset;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
}
