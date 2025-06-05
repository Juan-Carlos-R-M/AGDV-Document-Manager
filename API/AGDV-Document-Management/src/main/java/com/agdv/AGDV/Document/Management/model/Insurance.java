package com.agdv.AGDV.Document.Management.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "insurance")
public class Insurance {
    @Id
    private int id;
    private String name;
    
    public Insurance() {}

    public Insurance(int id, String nombre) {
        this.id = id;
        this.name = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
