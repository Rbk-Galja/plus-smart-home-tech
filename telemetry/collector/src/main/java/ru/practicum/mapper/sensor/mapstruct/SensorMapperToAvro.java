package ru.practicum.mapper.sensor.mapstruct;

import org.mapstruct.Mapper;
import ru.practicum.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface SensorMapperToAvro {

    ClimateSensorAvro toAvro(ClimateSensorEvent event);

    LightSensorAvro toAvro(LightSensorEvent event);

    MotionSensorAvro toAvro(MotionSensorEvent event);

    SwitchSensorAvro toAvro(SwitchSensorEvent event);

    TemperatureSensorAvro toAvro(TemperatureSensorEvent event);

    default SensorEventAvro toAvro(SensorEvent event) {
        Instant ts = event.getTimestamp();
        String eventId = event.getId();
        String hubId = event.getHubId();
        return switch (event) {
            case ClimateSensorEvent e -> new SensorEventAvro(eventId, hubId, ts, toAvro(e));
            case LightSensorEvent e -> new SensorEventAvro(eventId, hubId, ts, toAvro(e));
            case MotionSensorEvent e -> new SensorEventAvro(eventId, hubId, ts, toAvro(e));
            case SwitchSensorEvent e -> new SensorEventAvro(eventId, hubId, ts, toAvro(e));
            case TemperatureSensorEvent e -> new SensorEventAvro(eventId, hubId, ts, toAvro(e));
            default -> throw new IllegalArgumentException("Неопределенный sensor type: " + event.getClass());
        };
    }
}
