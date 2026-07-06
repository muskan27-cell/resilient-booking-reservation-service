package com.showcase.booking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    @Id
    private UUID id;

    @Column(nullable = false)
    private Long inventoryItemId;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int amountCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private String paymentReference;
    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt;

    public static Booking pending(Long inventoryItemId, String customerEmail, int quantity, int amountCents) {
        Booking booking = new Booking();
        booking.id = UUID.randomUUID();
        booking.inventoryItemId = inventoryItemId;
        booking.customerEmail = customerEmail;
        booking.quantity = quantity;
        booking.amountCents = amountCents;
        booking.status = BookingStatus.PENDING;
        booking.createdAt = Instant.now();
        return booking;
    }
}
