package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HubEventService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    public void handleEvent(HubEventAvro event) {
        String hubId = event.getHubId();
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            handleDeviceAdded(hubId, deviceAdded);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            handleDeviceRemoved(hubId, deviceRemoved);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(hubId, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(hubId, scenarioRemoved);
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro device) {
        if (!sensorRepository.existsById(device.getId())) {
            Sensor sensor = new Sensor();
            sensor.setId(device.getId());
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro device) {
        sensorRepository.findByIdAndHubId(device.getId(), hubId)
                .ifPresent(sensorRepository::delete);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioAdded.getName())
                .orElseGet(() -> {
                    Scenario newScenario = new Scenario();
                    newScenario.setHubId(hubId);
                    newScenario.setName(scenarioAdded.getName());
                    return newScenario;
                });
        scenario = scenarioRepository.save(scenario);

        List<ScenarioCondition> oldConditions = scenarioConditionRepository.findByScenarioId(scenario.getId());
        scenarioConditionRepository.deleteAll(oldConditions);

        List<ScenarioAction> oldActions = scenarioActionRepository.findByScenarioId(scenario.getId());
        scenarioActionRepository.deleteAll(oldActions);

        for (ScenarioConditionAvro condAvro : scenarioAdded.getConditions()) {
            Condition cond = new Condition();
            cond.setType(condAvro.getType().name());
            cond.setOperation(condAvro.getOperation().name());
            cond.setValue(condAvro.getValue() != null ? (Integer) condAvro.getValue() : null);
            cond = conditionRepository.save(cond);

            Sensor sensor = sensorRepository.findByIdAndHubId(condAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Датчик не найден для условия"));

            ScenarioConditionId scId = new ScenarioConditionId(scenario.getId(), sensor.getId(), cond.getId());
            ScenarioCondition sc = new ScenarioCondition(scId, scenario, sensor, cond);
            scenarioConditionRepository.save(sc);
        }

        for (DeviceActionAvro actAvro : scenarioAdded.getActions()) {
            Action act = new Action();
            act.setType(actAvro.getType().name());
            act.setValue(actAvro.getValue() != null ? (Integer) actAvro.getValue() : null);
            act = actionRepository.save(act);

            Sensor sensor = sensorRepository.findByIdAndHubId(actAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Датчик не найден для действия"));

            ScenarioActionId saId = new ScenarioActionId(scenario.getId(), sensor.getId(), act.getId());
            ScenarioAction sa = new ScenarioAction(saId, scenario, sensor, act);
            scenarioActionRepository.save(sa);
        }
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro scenarioRemoved) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioRemoved.getName())
                .orElseThrow(() -> new IllegalStateException("Сценарий не найден"));

        List<ScenarioCondition> conditions = scenarioConditionRepository.findByScenarioId(scenario.getId());
        scenarioConditionRepository.deleteAll(conditions);

        List<ScenarioAction> actions = scenarioActionRepository.findByScenarioId(scenario.getId());
        scenarioActionRepository.deleteAll(actions);

        scenarioRepository.delete(scenario);
    }
}

