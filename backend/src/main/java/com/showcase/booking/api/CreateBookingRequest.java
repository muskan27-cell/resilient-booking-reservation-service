package com.showcase.booking.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull Long inventoryItemId,
        @Email @NotBlank String customerEmail,
        @Min(1) int quantity,
        boolean forcePaymentFailure
) {
}
