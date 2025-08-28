package ru.practicum.model.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.action.event.DeviceAddedEvent;
import ru.practicum.model.action.event.DeviceRemoveEvent;
import ru.practicum.model.action.scenario.ScenarioAddedEvent;
import ru.practicum.model.action.scenario.ScenarioRemovedEvent;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = HubAction.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemoveEvent.class, name = "DEVICE_REMOVED")
})
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class HubAction {
    @NotBlank(message = "Указан пустой индитификатор хаба")
    String hubId;

    Instant timestamp = Instant.now();

    public abstract HubEventType getType();
}
