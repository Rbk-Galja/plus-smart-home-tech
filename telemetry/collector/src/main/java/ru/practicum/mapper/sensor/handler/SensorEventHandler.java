package ru.practicum.mapper.sensor.handler;

import ru.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();
    SensorEvent toJava(SensorEventProto protoEvent);
}
