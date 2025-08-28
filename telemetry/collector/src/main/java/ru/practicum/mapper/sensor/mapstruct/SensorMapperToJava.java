package ru.practicum.mapper.sensor.mapstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class SensorMapperToJava {
    private final SensorMapperToProto sensorMapperToProto;

    public SensorEvent toJava(SensorEventProto sensorEventProto) {
        return switch (sensorEventProto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT -> sensorMapperToProto.motionToJava(sensorEventProto);
            case TEMPERATURE_SENSOR_EVENT -> sensorMapperToProto.temperatureToJava(sensorEventProto);
            case LIGHT_SENSOR_EVENT -> sensorMapperToProto.lightToJava(sensorEventProto);
            case CLIMATE_SENSOR_EVENT -> sensorMapperToProto.climateToJava(sensorEventProto);
            case SWITCH_SENSOR_EVENT -> sensorMapperToProto.switchToJava(sensorEventProto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Не найден указанный тип датчика");
        };
    }
}
