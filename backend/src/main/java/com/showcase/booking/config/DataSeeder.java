package com.showcase.booking.config;

import com.showcase.booking.domain.InventoryItem;
import com.showcase.booking.domain.InventoryType;
import com.showcase.booking.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedInventory(InventoryRepository inventoryRepository) {
        return args -> {
            if (inventoryRepository.count() > 0) {
                return;
            }
            inventoryRepository.save(new InventoryItem(
                    InventoryType.FLIGHT,
                    "AI-DEL-SFO-042",
                    "Delhi to San Francisco - Economy Seat",
                    7,
                    72900
            ));
            inventoryRepository.save(new InventoryItem(
                    InventoryType.HOTEL_ROOM,
                    "BLR-BOUTIQUE-QUEEN",
                    "Bengaluru Boutique Hotel - Queen Room",
                    4,
                    9800
            ));
            inventoryRepository.save(new InventoryItem(
                    InventoryType.FLIGHT,
                    "UK-BOM-LHR-018",
                    "Mumbai to London - Premium Economy",
                    2,
                    112500
            ));
        };
    }
}
