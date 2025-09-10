package ru.practicum.mapper.sensor.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.sensor.*;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorMapperToProto {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "temperatureC", source = "proto.climateSensorEvent.temperatureC")
    @Mapping(target = "humidity", source = "climateSensorEvent.humidity")
    @Mapping(target = "co2Level", source = "climateSensorEvent.co2Level")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    ClimateSensorEvent climateToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "linkQuality", source = "lightSensorEvent.linkQuality")
    @Mapping(target = "luminosity", source = "lightSensorEvent.luminosity")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    LightSensorEvent lightToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "linkQuality", source = "proto.motionSensorEvent.linkQuality")
    @Mapping(target = "motion", source = "proto.motionSensorEvent.motion")
    @Mapping(target = "voltage", source = "proto.motionSensorEvent.voltage")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    MotionSensorEvent motionToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "state", source = "switchSensorEvent.state")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    SwitchSensorEvent switchToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "temperatureC", source = "proto.temperatureSensorEvent.temperatureC")
    @Mapping(target = "temperatureF", source = "proto.temperatureSensorEvent.temperatureF")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    TemperatureSensorEvent temperatureToJava(SensorEventProto proto);

    default Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}
