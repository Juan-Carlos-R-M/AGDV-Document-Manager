
package com.agdv.AGDV.Document.Management.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "document")
public class DocumentCar {
    @Id
    private String id;
    private String carId;
    private String date;
    private String file;
    private String name;
    private  int asset;

    public DocumentCar(String id, String carId, String date, String file, String name,int asset) {
        this.id = id;
        this.carId = carId;
        this.date = date;
        this.file = file;
        this.name = name;
        this.asset = asset;
    }

    public DocumentCar() {
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAsset() {
        return asset;
    }

    public void setAsset(int asset) {
        this.asset = asset;
    }
}
