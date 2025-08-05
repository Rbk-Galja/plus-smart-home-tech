package ru.practicum.model.action.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.action.HubAction;
import ru.practicum.model.action.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubAction {
    @NotBlank(message = "Указан пустой индитификатор устройства")
    private String id;

    @NotNull(message = "Указан пустой тип устройства")
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
