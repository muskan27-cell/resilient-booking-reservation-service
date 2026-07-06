package com.showcase.booking.service;

import com.showcase.booking.api.BookingResponse;
import com.showcase.booking.api.CreateBookingRequest;
import com.showcase.booking.domain.Booking;
import com.showcase.booking.domain.BookingStatus;
import com.showcase.booking.domain.InventoryItem;
import com.showcase.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingSagaService {
    private final InventoryService inventoryService;
    private final PaymentGatewayClient paymentGatewayClient;
    private final BookingRepository bookingRepository;

    public BookingSagaService(
            InventoryService inventoryService,
            PaymentGatewayClient paymentGatewayClient,
            BookingRepository bookingRepository
    ) {
        this.inventoryService = inventoryService;
        this.paymentGatewayClient = paymentGatewayClient;
        this.bookingRepository = bookingRepository;
    }

    public BookingResponse book(CreateBookingRequest request) {
        InventoryItem reserved = inventoryService.reserve(request.inventoryItemId(), request.quantity());
        Booking booking = Booking.pending(
                reserved.getId(),
                request.customerEmail(),
                request.quantity(),
                reserved.getPriceCents() * request.quantity()
        );
        bookingRepository.save(booking);

        try {
            String paymentReference = paymentGatewayClient.charge(
                    request.customerEmail(),
                    booking.getAmountCents(),
                    request.forcePaymentFailure()
            );
            return BookingResponse.from(confirm(booking, paymentReference));
        } catch (RuntimeException ex) {
            inventoryService.release(request.inventoryItemId(), request.quantity());
            return BookingResponse.from(compensate(booking, ex.getMessage()));
        }
    }

    @Transactional
    Booking confirm(Booking booking, String paymentReference) {
        booking.setPaymentReference(paymentReference);
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Transactional
    Booking compensate(Booking booking, String reason) {
        booking.setStatus(BookingStatus.COMPENSATED);
        booking.setFailureReason("Payment failed; inventory reservation released. " + reason);
        return bookingRepository.save(booking);
    }
}
