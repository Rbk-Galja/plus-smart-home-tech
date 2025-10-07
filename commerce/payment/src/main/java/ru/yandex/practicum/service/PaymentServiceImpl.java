package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.feign.client.OrderClient;
import ru.yandex.practicum.feign.client.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentStatus;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Формируем оплату для заказа {}", orderDto);
        Payment payment = Payment.builder()
                .id(orderDto.getOrderId())
                .totalPrice(orderDto.getTotalPrice())
                .deliveryPrice(orderDto.getDeliveryPrice())
                .productPrice(orderDto.getProductPrice())
                .feeTotal(orderDto.getTotalPrice().multiply(BigDecimal.valueOf(0.1)))
                .status(PaymentStatus.PENDING)
                .build();
        return mapper.toDto(repository.save(payment));
    }

    @Override
    public BigDecimal totalCost(OrderDto orderDto) {
        log.info("Рассчет полной стоимости заказа {}", orderDto);
        return orderDto.getProductPrice().add(orderDto.getProductPrice().multiply(BigDecimal.valueOf(0.1))
                .add(orderDto.getDeliveryPrice()));
    }

    @Override
    @Transactional
    public void refundOrder(UUID paymentId) {
        log.info("Изменение статуса оплаты id = {} на SUCCESS", paymentId);
        Payment payment = repository.findById(paymentId).orElseThrow(() -> new NotFoundException("Оплата не найдена"));
        payment.setStatus(PaymentStatus.SUCCESS);
        orderClient.payment(payment.getOrderId());
    }

    @Override
    public BigDecimal calculateOrderTotal(OrderDto orderDto) {
        log.info("Рассчет стоимости товаров в заказе {}", orderDto);
        Map<UUID, BigDecimal> prices = shoppingStoreClient.getProductPrices(
                new ArrayList<>(orderDto.getProducts().keySet())
        );
        log.info("Рассчет стоимости произведен успешно");
        return orderDto.getProducts().entrySet().stream()
                .map(entry -> prices.get(entry.getKey())
                        .multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void failedPayment(UUID paymentId) {
        log.info("Изменение статуса оплаты id = {} на FAILED", paymentId);
        Payment payment = repository.findById(paymentId).orElseThrow(() -> new NotFoundException("Оплата не найдена"));
        payment.setStatus(PaymentStatus.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
    }
}
