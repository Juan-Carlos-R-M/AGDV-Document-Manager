package com.agdv.AGDV.Document.Management.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private MongoClient mongoClient;

    @GetMapping("/")
    public String home() {
        try {
            MongoDatabase db = mongoClient.getDatabase("agdv");
            Document ping = new Document("ping", 1);
            db.runCommand(ping); // esto lanza error si no hay conexión

            return "✅ Proyecto corriendo y conectado exitosamente a la base de datos: " + db.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Proyecto corriendo, pero error al conectar con la base de datos: " + e.getMessage();
        }
    }
}
