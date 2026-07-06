package com.showcase.booking.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryType type;

    @NotBlank
    @Column(nullable = false)
    private String sku;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @PositiveOrZero
    @Column(nullable = false)
    private int available;

    @PositiveOrZero
    @Column(nullable = false)
    private int priceCents;

    @Version
    private long version;

    public InventoryItem(InventoryType type, String sku, String name, int available, int priceCents) {
        this.type = type;
        this.sku = sku;
        this.name = name;
        this.available = available;
        this.priceCents = priceCents;
    }
}
