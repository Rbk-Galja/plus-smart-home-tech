package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.username = :username")
    Optional<Cart> findByUserNameWithItems(@Param("username") String username);
}
