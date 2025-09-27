package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProduct {
    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    UUID productId;

    @Column(nullable = false)
    boolean fragile;

    @Column(nullable = false)
    double weight;

    @Column(nullable = false)
    double width;

    @Column(nullable = false)
    double height;

    @Column(nullable = false)
    double depth;

    @Column(nullable = false)
    long quantity;
}
