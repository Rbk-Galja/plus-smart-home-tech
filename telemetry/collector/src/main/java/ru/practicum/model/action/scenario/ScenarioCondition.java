package ru.practicum.model.action.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.sensor.SensorType;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensorId;
    private SensorType type;
    private OperationType operation;
    private Integer value;
}
