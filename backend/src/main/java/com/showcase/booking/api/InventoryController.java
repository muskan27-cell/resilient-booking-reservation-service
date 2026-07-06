package com.showcase.booking.api;

import com.showcase.booking.repository.InventoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    List<InventoryResponse> list() {
        return inventoryRepository.findAll().stream().map(InventoryResponse::from).toList();
    }
}
