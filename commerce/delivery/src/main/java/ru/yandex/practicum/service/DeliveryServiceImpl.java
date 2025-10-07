package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.feign.client.OrderClient;
import ru.yandex.practicum.feign.client.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryState;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {
    private static final BigDecimal DEFAULT_COST = new BigDecimal("5");
    private static final BigDecimal WAREHOUSE_ADDRESS_MULTIPLE = new BigDecimal("2");
    private static final BigDecimal FRAGILE_MULTIPLE = new BigDecimal("0.2");
    private static final BigDecimal DELIVERY_WEIGHT_MULTIPLE = new BigDecimal("0.3");
    private static final BigDecimal DELIVERY_VOLUME_MULTIPLE = new BigDecimal("0.3");
    private static final BigDecimal DELIVERY_DISTANCE_MULTIPLE = new BigDecimal("0.2");
    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public DeliveryDto createOrder(DeliveryDto deliveryDto) {
        log.info("Создаем новую доставку в БД {}", deliveryDto);
        return mapper.toDeliveryDto(repository.save(mapper.toDelivery(deliveryDto)));
    }

    @Override
    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        log.info("Изменение статуса заявки id = {} на DELIVERED", deliveryId);
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.complete(delivery.getOrderId());
    }

    @Override
    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        log.info("Изменение статуса заявки id = {} на IN_PROGRESS", deliveryId);
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(deliveryRequest);
    }

    @Override
    @Transactional
    public void failedDelivery(UUID deliveryId) {
        log.info("Изменение статуса заявки id = {} на FAILED", deliveryId);
        Delivery delivery = repository.findById(deliveryId).orElseThrow(NotFoundException::new);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.assemblyFailed(delivery.getOrderId());
    }

    @Override
    public BigDecimal costDelivery(OrderDto orderDto) {
        log.info("Начинаем рассчет полной стоимости доставки заказа {}", orderDto);
        Delivery delivery = repository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NotFoundException("Доставка не найдена"));
        BigDecimal cost = DEFAULT_COST;
        AddressDto warehouseAddress = warehouseClient.getCurrentWarehouseAddress();
        if ("ADDRESS_2".equals(warehouseAddress.getCity())) {
            cost = cost.add(cost.multiply(WAREHOUSE_ADDRESS_MULTIPLE));
        }
        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            cost = cost.add(cost.multiply(FRAGILE_MULTIPLE));
        }

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(DELIVERY_WEIGHT_MULTIPLE));

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(DELIVERY_VOLUME_MULTIPLE));

        if (warehouseAddress.getCity().equals(delivery.getToAddress().getCity())
                && warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            return cost;
        } else {
            return cost.add(cost.multiply(DELIVERY_DISTANCE_MULTIPLE));
        }
    }
}
