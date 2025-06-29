// GridFSConfig.java
package com.agdv.AGDV.Document.Management.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridFSConfig {

    private final MongoClient mongoClient;

    public GridFSConfig(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Bean
    public GridFSBucket gridFSBucket() {
        MongoDatabase database = mongoClient.getDatabase("agdv");
        return GridFSBuckets.create(database, "backup");
    }
}