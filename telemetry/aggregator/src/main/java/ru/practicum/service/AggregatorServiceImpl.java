package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AggregatorServiceImpl implements AggregatorService {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> aggregate(SensorEventAvro event) {
        SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.computeIfAbsent(event.getHubId(), hubId -> {
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setHubId(hubId);
            newSnapshot.setSensorsState(new HashMap<>());
            return newSnapshot;
        });

        if (sensorsSnapshotAvro.getSensorsState().containsKey(event.getId())) {
            SensorStateAvro oldState = sensorsSnapshotAvro.getSensorsState().get(event.getId());
            if (event.getTimestamp().isBefore(oldState.getTimestamp()) ||
                    oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        sensorsSnapshotAvro.getSensorsState().put(event.getId(), newState);
        sensorsSnapshotAvro.setTimestamp(event.getTimestamp());
        snapshots.put(sensorsSnapshotAvro.getHubId(), sensorsSnapshotAvro);

        return Optional.of(sensorsSnapshotAvro);
    }
}
