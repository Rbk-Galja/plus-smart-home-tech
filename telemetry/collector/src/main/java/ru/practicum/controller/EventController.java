package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mapper.HubMapper;
import ru.practicum.mapper.SensorMapper;
import ru.practicum.model.action.HubAction;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.producer.KafkaProducerConfig;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final KafkaProducerConfig.EventProducer producer;
    private final HubMapper hubMapper;
    private final SensorMapper sensorMapper;

    @Value("${kafka.topic.hubs}")
    private String hubTopic;

    @Value("${kafka.topic.sensors}")
    private String sensorTopic;


    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        SensorEventAvro avro = sensorMapper.toAvro(event);
        long timestamp = event.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                sensorTopic,
                null,
                timestamp,
                event.getHubId(),
                avro);

        producer.getProducer().send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке sensor event id={}, Error: {}", event.getId(), exception.getMessage());
                throw new RuntimeException("Произошла ошибка при отправке sensor event: ", exception);
            } else {
                log.info("Отправка sensor event прошла успешно. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubAction event) {
        HubEventAvro avro = hubMapper.toAvro(event);
        long timestamp = event.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                hubTopic,
                null,
                timestamp,
                event.getHubId(),
                avro
        );

        producer.getProducer().send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке hub event. HubId={}, Error: {}",
                        event.getHubId(), exception.getMessage());
                throw new RuntimeException("Ошибка отправки hub event", exception);
            } else {
                log.info("Отправка hub event прошла успешно. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
