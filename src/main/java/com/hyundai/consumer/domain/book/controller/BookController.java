package com.hyundai.consumer.domain.book.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cqrs")
@Tag(name = "Books", description = "도서 조회 API")
public class BookController {

    private final MongoDatabase mongoDatabase;

    public BookController(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @GetMapping("/book")
    @Operation(summary = "도서 목록 조회", description = "Kafka를 통해 동기화된 도서 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "도서 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(type = "object"))))
    public ResponseEntity<List<Map<String, Object>>> getBooks() {
        MongoCollection<Document> booksCollection = mongoDatabase.getCollection("books");

        List<Document> documents = booksCollection.find().into(new ArrayList<>());
        List<Map<String, Object>> books = documents.stream()
                .map(BookController::toMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(books);
    }

    private static Map<String, Object> toMap(Document document) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            result.put(entry.getKey(), normalizeValue(entry.getValue()));
        }
        return result;
    }

    private static Object normalizeValue(Object value) {
        if (value instanceof Document) {
            return toMap((Document) value);
        }
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .map(BookController::normalizeValue)
                    .collect(Collectors.toList());
        }
        if (value instanceof ObjectId) {
            return value.toString();
        }
        return value;
    }
}
