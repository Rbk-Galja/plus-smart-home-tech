package ru.practicum.mapper.sensor.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mapper.sensor.mapstruct.SensorMapperToProto;
import ru.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {
    private final SensorMapperToProto sensorMapperToProto;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public SensorEvent toJava(SensorEventProto protoEvent) {
        return sensorMapperToProto.lightToJava(protoEvent);
    }
}
