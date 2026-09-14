package com.hyundai.consumer.global.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private MongoCollection<Document> books;

    @Test
    void consumeSavesTheEntireBookMessage() {
        when(mongoDatabase.getCollection("books")).thenReturn(books);
        KafkaConsumer consumer = new KafkaConsumer(mongoDatabase);
        String message = """
                {
                  "bid": 1,
                  "title": "채식주의자",
                  "author": "한강",
                  "category": "소설",
                  "pages": 224,
                  "price": 15000,
                  "publiched_date": "2007-10-30",
                  "description": "한 여성의 삶을 다룬 장편소설"
                }
                """;

        consumer.consume(message);

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(books).insertOne(documentCaptor.capture());
        Document savedBook = documentCaptor.getValue();
        assertEquals(1, savedBook.getInteger("bid"));
        assertEquals("채식주의자", savedBook.getString("title"));
        assertEquals("한강", savedBook.getString("author"));
        assertEquals(224, savedBook.getInteger("pages"));
        assertEquals(15000, savedBook.getInteger("price"));
        assertEquals("2007-10-30", savedBook.getString("publiched_date"));
    }
}
