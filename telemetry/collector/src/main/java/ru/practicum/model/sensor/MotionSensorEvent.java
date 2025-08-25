package ru.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MotionSensorEvent extends SensorEvent {
    @NotNull(message = "Указано пустое значение качества связи")
    Integer linkQuality;
    @NotNull(message = "Указано пустое значение наличия движения")
    Boolean motion;
    @NotNull(message = "Указано пустое значение напряжения")
    Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
