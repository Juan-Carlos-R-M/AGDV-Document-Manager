package com.agdv.AGDV.Document.Management.model;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "backups")
public class Backup {
    @Id
    private String id;
    private String name;
    private String date;
    private String size;
    
        public Backup(String id, String name, String date, String size, String path) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.size = size;
    }

    public Backup() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

}
