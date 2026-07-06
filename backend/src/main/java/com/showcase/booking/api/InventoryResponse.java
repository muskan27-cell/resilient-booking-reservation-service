package com.showcase.booking.api;

import com.showcase.booking.domain.InventoryItem;
import com.showcase.booking.domain.InventoryType;

public record InventoryResponse(
        Long id,
        InventoryType type,
        String sku,
        String name,
        int available,
        int priceCents,
        long version
) {
    public static InventoryResponse from(InventoryItem item) {
        return new InventoryResponse(
                item.getId(),
                item.getType(),
                item.getSku(),
                item.getName(),
                item.getAvailable(),
                item.getPriceCents(),
                item.getVersion()
        );
    }
}
