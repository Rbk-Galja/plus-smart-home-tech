package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false)
    UUID orderId;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal productPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal deliveryPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal totalPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    PaymentStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;
    @UpdateTimestamp
    @CreationTimestamp
    LocalDateTime updatedAt;
}
