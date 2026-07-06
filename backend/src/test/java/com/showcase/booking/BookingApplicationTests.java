package com.showcase.booking;

import com.showcase.booking.api.CreateBookingRequest;
import com.showcase.booking.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "simulation.random-payment-failure-rate=0"
)
class BookingApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    InventoryRepository inventoryRepository;

    @Test
    void sameIdempotencyKeyReturnsSameBookingAndDoesNotDoubleReserveInventory() {
        long itemId = inventoryRepository.findBySku("AI-DEL-SFO-042").orElseThrow().getId();
        int before = inventoryRepository.findById(itemId).orElseThrow().getAvailable();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Idempotency-Key", "test-key-123");
        HttpEntity<CreateBookingRequest> entity = new HttpEntity<>(
                new CreateBookingRequest(itemId, "traveler@example.com", 1, false),
                headers
        );

        ResponseEntity<Map> first = restTemplate.postForEntity("/api/bookings", entity, Map.class);
        ResponseEntity<Map> second = restTemplate.postForEntity("/api/bookings", entity, Map.class);

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(second.getBody()).isEqualTo(first.getBody());
        assertThat(inventoryRepository.findById(itemId).orElseThrow().getAvailable()).isEqualTo(before - 1);
    }
}
