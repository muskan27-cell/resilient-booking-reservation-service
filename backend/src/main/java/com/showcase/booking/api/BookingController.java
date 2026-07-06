package com.showcase.booking.api;

import com.showcase.booking.repository.BookingRepository;
import com.showcase.booking.service.BookingSagaService;
import com.showcase.booking.service.IdempotencyService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingSagaService bookingSagaService;
    private final BookingRepository bookingRepository;
    private final IdempotencyService idempotencyService;

    public BookingController(
            BookingSagaService bookingSagaService,
            BookingRepository bookingRepository,
            IdempotencyService idempotencyService
    ) {
        this.bookingSagaService = bookingSagaService;
        this.bookingRepository = bookingRepository;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> create(
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        return idempotencyService.execute(idempotencyKey, request, () -> bookingSagaService.book(request));
    }

    @GetMapping
    List<BookingResponse> list() {
        return bookingRepository.findAll().stream().map(BookingResponse::from).toList();
    }

    @GetMapping("/{id}")
    BookingResponse get(@PathVariable UUID id) {
        return bookingRepository.findById(id)
                .map(BookingResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
}
