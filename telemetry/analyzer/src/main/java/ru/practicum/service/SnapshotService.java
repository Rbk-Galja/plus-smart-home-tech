package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.manager.ScenarioManager;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioManager scenarioManager;

    @Transactional
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        scenarioManager.evaluateSnapshot(snapshot);
    }
}
