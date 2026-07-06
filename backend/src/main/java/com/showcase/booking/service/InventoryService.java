package com.showcase.booking.service;

import com.showcase.booking.domain.InventoryItem;
import com.showcase.booking.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryItem reserve(Long inventoryItemId, int quantity) {
        InventoryItem item = inventoryRepository.findById(inventoryItemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        if (item.getAvailable() < quantity) {
            throw new IllegalArgumentException("Not enough inventory for " + item.getSku());
        }
        item.setAvailable(item.getAvailable() - quantity);
        return inventoryRepository.saveAndFlush(item);
    }

    @Transactional
    public void release(Long inventoryItemId, int quantity) {
        InventoryItem item = inventoryRepository.findById(inventoryItemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        item.setAvailable(item.getAvailable() + quantity);
        inventoryRepository.save(item);
    }
}
