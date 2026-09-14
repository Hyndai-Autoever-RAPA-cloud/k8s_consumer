package com.hyundai.consumer.global.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final String TOPIC = "cqrs-topic";
    private static final String BOOKS_COLLECTION = "books";
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final MongoDatabase mongoDatabase;

    public KafkaConsumer(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        Document book = Document.parse(message);
        MongoCollection<Document> books = mongoDatabase.getCollection(BOOKS_COLLECTION);

        books.insertOne(book);
        log.info("Kafka message saved: topic={}, bid={}", TOPIC, book.get("bid"));
    }
}
