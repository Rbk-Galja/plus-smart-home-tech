package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.CartDeactivatedException;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.CartItemId;
import ru.yandex.practicum.repository.CartRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository repository;
    private final ShoppingCartMapper mapper;

    @Override
    @Transactional
    public ShoppingCartDto getCart(String username) {
        log.info("Начинаем получение корзины для пользователя {}", username);
        Cart cart = repository.findByUserNameWithItems(username)
                .orElseGet(() -> createCart(username));
        log.info("Получение корзины для пользователя {} завершено: {}", username, cart);
        return mapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductsInCart(String username, Map<UUID, Long> newProduct) {
        log.info("Начинаем добавление товаров в корзину для пользователя {}", username);
        Cart cart = repository.findByUserNameWithItems(username)
                .orElseGet(() -> createCart(username));
        if (!cart.isActive()) {
            log.error("Корзина для пользователя {} находится в деактивированном состоянии", username);
            throw new CartDeactivatedException("Корзина пользователя деактивирвоана", "Корзина для пользователя "
                    + username + " деактивирована");
        }
        Map<UUID, CartItem> cartItemMap = cart.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getId().getProductId(),
                        Function.identity()
                ));

        newProduct.forEach((productId, quantity) -> {
            if (cartItemMap.containsKey(productId)) {
                CartItem existItem = cartItemMap.get(productId);
                existItem.setQuantity(existItem.getQuantity() + quantity);
            } else {
                CartItem newItem = CartItem.builder()
                        .id(new CartItemId(cart.getCartId(), productId))
                        .cart(cart)
                        .quantity(quantity)
                        .build();
                cart.getItems().add(newItem);
            }
        });

        Cart savedCart = repository.save(cart);
        log.info("Добавление товаров в корзину пользователя {} завершено: {}", username, savedCart);
        return mapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public void deleteCart(String username) {
        log.info("Начинаем деактивацию корзины для пользователя {}", username);
        Cart cart = repository.findByUserNameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина для пользователя не найдена : " + username));
        cart.setActive(false);
        log.info("Деактивация корзины для пользователя {} прошла успешно", username);
    }

    @Override
    @Transactional
    public void deleteProductFromCart(String username, List<UUID> productIds) {
        log.info("Начинаем удаление товара из корзины пользователя {}", username);
        Cart cart = repository.findByUserNameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина для пользователя " + username + " не найдена"));
        if (!cart.isActive()) {
            log.error("Корзина пользователя {} находится в деактивированном состоянии", username);
            throw new CartDeactivatedException("Корзина пользователя деактивирована", "Корзина пользователя "
                    + username + " деактивирована");
        }

        List<CartItem> toRemove = cart.getItems().stream()
                .filter(item -> productIds.contains(item.getId().getProductId()))
                .toList();

        if (toRemove.size() != productIds.size()) {
            log.error("Некоторые из удаляемых товаров не найдены в корзине");
            throw new NoProductsInShoppingCartException("Некоторые товары не найдены", "Некоторые товары в корзине " +
                    "пользователя " + username + " отсутствуют");
        }

        toRemove.forEach(cart.getItems()::remove);
        log.info("Удаление товаров прошло успешно");
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Начинаем изменение количества товаров в корзине");
        Cart cart = repository.findByUserNameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена для пользователя: " + username));

        if (!cart.isActive()) {
            log.error("Корзина пользователя {} деактивирована", username);
            throw new CartDeactivatedException("Корзина пользователя деактивирована", "Корзина для пользователя "
                    + username + " деактивирована");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Товара с таким id нет в корзине",
                        "Товар с Id " + request.getProductId() + " не найден в корзине"
                ));
        cartItem.setQuantity(request.getNewQuantity());
        log.info("Обновление количества товара в корзине пользователя {} прошло успешно", username);
        return mapper.toDto(cart);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private Cart createCart(String username) {
        log.info("Создаем корзину для пользователя {}", username);
        return repository.save(
                Cart.builder()
                        .items(new HashSet<>())
                        .username(username)
                        .build()
        );
    }
}
