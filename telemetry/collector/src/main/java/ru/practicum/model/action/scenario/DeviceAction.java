package ru.practicum.model.action.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAction {
    private String sensorId;
    private ActionsType type;
    private Integer value;
}
