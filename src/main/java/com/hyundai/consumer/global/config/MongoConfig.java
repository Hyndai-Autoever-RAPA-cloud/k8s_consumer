package com.hyundai.consumer.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoDatabase mongoDatabase(
            MongoClient mongoClient,
            @Value("${spring.mongodb.database}") String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }
}
