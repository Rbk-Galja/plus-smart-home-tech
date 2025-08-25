package ru.practicum.manager;

import lombok.RequiredArgsConstructor;
import com.google.protobuf.util.Timestamps;
import org.springframework.stereotype.Component;
import ru.practicum.client.HubRouterClient;
import ru.practicum.model.*;
import ru.practicum.repository.ScenarioActionRepository;
import ru.practicum.repository.ScenarioConditionRepository;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioManager {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final SensorConditionManager registry;
    private final HubRouterClient hubRouterClient;

    public void evaluateSnapshot(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());

        for (Scenario scenario : scenarios) {
            List<ScenarioCondition> scenarioConditions = scenarioConditionRepository.findByScenarioId(scenario.getId());

            boolean conditionsOk = scenarioConditions.stream()
                    .allMatch(sc -> {
                        String sensorId = sc.getSensor().getId();

                        // Получаем значение напрямую из снимка
                        if (!snapshot.getSensorsState().containsKey(sensorId)) {
                            return false;
                        }

                        return registry.extractValue(
                                        ConditionType.valueOf(sc.getCondition().getType()),
                                        snapshot.getSensorsState().get(sensorId)
                                )
                                .map(val -> registry.compare(
                                        Operation.valueOf(sc.getCondition().getOperation()),
                                        val,
                                        BigDecimal.valueOf(sc.getCondition().getValue())
                                ))
                                .orElse(false);
                    });

            if (conditionsOk) {
                List<ScenarioAction> scenarioActions = scenarioActionRepository.findByScenarioId(scenario.getId());

                scenarioActions.forEach(sa -> {
                    Action act = sa.getAction();
                    Sensor sensor = sa.getSensor();

                    DeviceActionProto actionProto = registry.buildAction(
                            ActionType.valueOf(act.getType()),
                            sensor.getId(),
                            act.getValue()
                    );

                    DeviceActionRequest request = DeviceActionRequest.newBuilder()
                            .setHubId(scenario.getHubId())
                            .setScenarioName(scenario.getName())
                            .setAction(actionProto)
                            .setTimestamp(Timestamps.fromMillis(snapshot.getTimestamp().toEpochMilli()))
                            .build();

                    hubRouterClient.sendAction(request);
                });
            }
        }
    }
}
