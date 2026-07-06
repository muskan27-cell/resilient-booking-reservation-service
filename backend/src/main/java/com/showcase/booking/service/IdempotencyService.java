package com.showcase.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.showcase.booking.api.CreateBookingRequest;
import com.showcase.booking.domain.IdempotencyRecord;
import com.showcase.booking.repository.IdempotencyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class IdempotencyService {
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyRepository idempotencyRepository, ObjectMapper objectMapper) {
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResponseEntity<String> execute(String idempotencyKey, CreateBookingRequest request, Supplier<Object> handler) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        String requestHash = hash(request);
        Optional<IdempotencyRecord> existing = idempotencyRepository.findById(idempotencyKey);
        if (existing.isPresent()) {
            IdempotencyRecord record = existing.get();
            if (!record.getRequestHash().equals(requestHash)) {
                throw new IllegalArgumentException("Idempotency-Key was reused with a different request body");
            }
            return ResponseEntity.status(record.getStatusCode()).body(record.getResponseBody());
        }

        Object response = handler.get();
        String body = toJson(response);
        idempotencyRepository.save(new IdempotencyRecord(
                idempotencyKey,
                requestHash,
                HttpStatus.CREATED.value(),
                body
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    private String hash(CreateBookingRequest request) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(request);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (JsonProcessingException | NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to hash request", ex);
        }
    }

    private String toJson(Object response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            return "{\"error\":\"serialization_failed\",\"message\":\"" +
                    ex.getMessage().replace("\"", "'").getBytes(StandardCharsets.UTF_8).length + "\"}";
        }
    }
}
