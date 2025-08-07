package ru.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {
    @NotNull(message = "Указано пустое значение качества связи")
    private Integer linkQuality;
    @NotNull(message = "Указано пустое значение наличия движения")
    private Boolean motion;
    @NotNull(message = "Указано пустое значение напряжения")
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
