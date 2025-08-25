package ru.practicum.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.model.ActionType;
import ru.practicum.model.ConditionType;
import ru.practicum.model.Operation;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SensorConditionManager {
    private final Map<ConditionType, Function<SensorStateAvro, Optional<BigDecimal>>> extractors =
            Map.of(
                    ConditionType.TEMPERATURE, st -> {
                        Object data = st.getData();
                        if (data instanceof TemperatureSensorAvro temp) {
                            return Optional.of(BigDecimal.valueOf(temp.getTemperatureC()));
                        }
                        if (data instanceof ClimateSensorAvro climate) {
                            return Optional.of(BigDecimal.valueOf(climate.getTemperatureC()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.HUMIDITY, st -> {
                        if (st.getData() instanceof ClimateSensorAvro climate) {
                            return Optional.of(BigDecimal.valueOf(climate.getHumidity()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.CO2LEVEL, st -> {
                        if (st.getData() instanceof ClimateSensorAvro climate) {
                            return Optional.of(BigDecimal.valueOf(climate.getCo2Level()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.LUMINOSITY, st -> {
                        if (st.getData() instanceof LightSensorAvro light) {
                            return Optional.of(BigDecimal.valueOf(light.getLuminosity()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.MOTION, st -> {
                        if (st.getData() instanceof MotionSensorAvro motion) {
                            return Optional.of(motion.getMotion() ? BigDecimal.ONE : BigDecimal.ZERO);
                        }
                        return Optional.empty();
                    },
                    ConditionType.SWITCH, st -> {
                        if (st.getData() instanceof SwitchSensorAvro sw) {
                            return Optional.of(sw.getState() ? BigDecimal.ONE : BigDecimal.ZERO);
                        }
                        return Optional.empty();
                    }
            );

    private final Map<Operation, BiPredicate<BigDecimal, BigDecimal>> comparators =
            Map.of(
                    Operation.GREATER_THAN, (a, b) -> a.compareTo(b) > 0,
                    Operation.LOWER_THAN, (a, b) -> a.compareTo(b) < 0,
                    Operation.EQUALS, (a, b) -> a.compareTo(b) == 0
            );


    private final Map<ActionType, BiFunction<String, Integer, DeviceActionProto>> actionBuilders =
            Map.of(
                    ActionType.ACTIVATE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.ACTIVATE)
                                    .build(),
                    ActionType.DEACTIVATE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.DEACTIVATE)
                                    .build(),
                    ActionType.INVERSE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.INVERSE)
                                    .build(),
                    ActionType.SET_VALUE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.SET_VALUE)
                                    .setValue(value)
                                    .build()
            );


    public Optional<BigDecimal> extractValue(ConditionType type, SensorStateAvro state) {
        return extractors.getOrDefault(type, st -> Optional.empty()).apply(state);
    }

    public boolean compare(Operation op, BigDecimal left, BigDecimal right) {
        return comparators.getOrDefault(op, (a, b) -> false).test(left, right);
    }

    public DeviceActionProto buildAction(ActionType type, String sensorId, Integer value) {
        return actionBuilders.getOrDefault(type,
                        (id, v) -> DeviceActionProto.getDefaultInstance())
                .apply(sensorId, value);
    }
}
