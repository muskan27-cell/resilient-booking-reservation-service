package com.showcase.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class IdempotencyRecord {
    @Id
    private String idempotencyKey;

    @Column(nullable = false)
    private String requestHash;

    @Column(nullable = false)
    private int statusCode;

    @Lob
    @Column(nullable = false)
    private String responseBody;

    @Column(nullable = false)
    private Instant createdAt;

    public IdempotencyRecord(String idempotencyKey, String requestHash, int statusCode, String responseBody) {
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.createdAt = Instant.now();
    }
}
