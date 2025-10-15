package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.feign.client.CartClient;
import ru.yandex.practicum.feign.client.DeliveryClient;
import ru.yandex.practicum.feign.client.PaymentClient;
import ru.yandex.practicum.feign.client.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.OrderStateDto;
import ru.yandex.practicum.order.ProductReturnRequest;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.BookedProductsDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartClient shoppingCartClient;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username, Integer page, Integer size) {
        log.info("Начинаем получение всех заказов пользователя {}", username);
        if (username == null || username.isBlank()) {
            log.error("Указано пустое имя пользователя");
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        }
        ShoppingCartDto shoppingCart = shoppingCartClient.getCart(username);

        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);
        List<Order> orders = orderRepository.findByShoppingCartId(
                shoppingCart.getShoppingCartId(), pageRequest);
        log.info("Получение заказов пользователя прошло успешно: {}", orders);
        return orders.stream().map(orderMapper::toOrderDto).toList();
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Начинаем создание нового заказа в системе {}", request);
        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .orderStateDto(OrderStateDto.NEW)
                .build();
        Order savedOrder = orderRepository.save(order);

        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(
                        request.getShoppingCart().getProducts(),
                        savedOrder.getOrderId()
                )
        );

        savedOrder.setFragile(bookedProducts.getFragile());
        savedOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        savedOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());

        savedOrder.setProductPrice(paymentClient.calculateOrderTotal(orderMapper.toOrderDto(savedOrder)));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(savedOrder.getOrderId())
                .fromAddress(warehouseClient.getCurrentWarehouseAddress())
                .toAddress(request.getDeliveryAddress())
                .build();
        savedOrder.setDeliveryId(deliveryClient.createOrder(deliveryDto).getDeliveryId());

        paymentClient.createPayment(orderMapper.toOrderDto(savedOrder));
        log.info("Заказ успешно сохранен {}", savedOrder);
        return orderMapper.toOrderDto(savedOrder);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Начинаем возврат товара {}", request);
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));

        warehouseClient.returnedProduct(request.getProducts());
        order.setOrderStateDto(OrderStateDto.PRODUCT_RETURNED);
        log.info("Возврат товара {} прошел успешно", order);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Прием оплаты заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.PAID);
        log.info("Оплата заказа  id = {} прошла успешно", orderId);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Обработка ошибки оплаты заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.PAYMENT_FAILED);
        log.info("Обработка ошибки оплаты заказа id = {} прошла успешно", orderId);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Обработка запроса доставки заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.DELIVERED);
        log.info("Обработка запроса на доставку заказа id = {} прошла успешно", orderId);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Обработка ошибки доставки заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.DELIVERY_FAILED);
        log.info("Обработка ошибки доставки заказа id = {} прошла успешно", orderId);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("Обработка завершения заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.COMPLETED);
        log.info("Обработка завершения заказа id = {} прошла успешно", orderId);
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Рассчитываем стоимость заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setTotalPrice(paymentClient.totalCost(orderMapper.toOrderDto(order)));
        log.info("Рассчет стоимости заказа прошел успешно: {}", order.getTotalPrice());
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Рассчитываем стоимость доставки заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setDeliveryPrice(deliveryClient.costDelivery(orderMapper.toOrderDto(order)));
        log.info("Рассчет стоимости доставки прошел успешно: {}", order.getDeliveryPrice());
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Обрабатываем запрос на сборку заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.ASSEMBLED);
        log.info("Запрос на сборку обработан успешно");
        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Обрабатываем ошибку сборки заказа id = {}", orderId);
        Order order = findOrderOrThrow(orderId);
        order.setOrderStateDto(OrderStateDto.ASSEMBLY_FAILED);
        log.info("Ошибка сборки заказа id = {} прошла успешно", orderId);
        return orderMapper.toOrderDto(order);
    }

    private Order findOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
    }
}
