package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Sensor;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    Optional<Sensor> findByIdAndHubId(String id, String hubId);
}
