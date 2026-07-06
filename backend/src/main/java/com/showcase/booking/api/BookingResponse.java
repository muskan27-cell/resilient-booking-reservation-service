package com.showcase.booking.api;

import com.showcase.booking.domain.Booking;
import com.showcase.booking.domain.BookingStatus;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        Long inventoryItemId,
        String customerEmail,
        int quantity,
        int amountCents,
        BookingStatus status,
        String paymentReference,
        String failureReason,
        Instant createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getInventoryItemId(),
                booking.getCustomerEmail(),
                booking.getQuantity(),
                booking.getAmountCents(),
                booking.getStatus(),
                booking.getPaymentReference(),
                booking.getFailureReason(),
                booking.getCreatedAt()
        );
    }
}
