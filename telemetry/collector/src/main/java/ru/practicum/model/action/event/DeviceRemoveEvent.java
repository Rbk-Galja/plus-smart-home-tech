package ru.practicum.model.action.event;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.action.HubAction;
import ru.practicum.model.action.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemoveEvent extends HubAction {
    @NotBlank(message = "Указан пустой индитификатор удаляемого устройства")
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
