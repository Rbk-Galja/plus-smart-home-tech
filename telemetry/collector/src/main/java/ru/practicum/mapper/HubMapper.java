package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.action.HubAction;
import ru.practicum.model.action.event.DeviceAddedEvent;
import ru.practicum.model.action.event.DeviceRemoveEvent;
import ru.practicum.model.action.scenario.ScenarioAddedEvent;
import ru.practicum.model.action.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface HubMapper {
    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toAvro(DeviceAddedEvent event);

    DeviceRemovedEventAvro toAvro(DeviceRemoveEvent event);

    ScenarioAddedEventAvro toAvro(ScenarioAddedEvent event);

    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event);

    default HubEventAvro toAvro(HubAction event) {
        Instant ts = event.getTimestamp();
        String hubId = event.getHubId();
        return switch (event) {
            case DeviceAddedEvent e -> new HubEventAvro(hubId, ts, toAvro(e));
            case DeviceRemoveEvent e -> new HubEventAvro(hubId, ts, toAvro(e));
            case ScenarioAddedEvent e -> new HubEventAvro(hubId, ts, toAvro(e));
            case ScenarioRemovedEvent e -> new HubEventAvro(hubId, ts, toAvro(e));
            default -> throw new IllegalArgumentException("Неопределенный hub type: " + event.getClass());
        };
    }
}
