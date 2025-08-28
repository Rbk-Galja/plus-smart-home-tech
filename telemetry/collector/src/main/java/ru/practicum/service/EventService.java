package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.hub.mapstruct.HubMapperToAvro;
import ru.practicum.mapper.sensor.mapstruct.SensorMapperToAvro;
import ru.practicum.model.action.HubAction;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.producer.KafkaProducerConfig;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final SensorMapperToAvro sensorMapperToAvro;
    private final HubMapperToAvro hubMapperToAvro;
    private final KafkaProducerConfig.EventProducer kafkaProducer;

    @Value("${kafka.topic.hubs}")
    private String hubTopic;

    @Value("${kafka.topic.sensors}")
    private String sensorTopic;

    public void processSensor(SensorEvent sensorEvent) {
        SensorEventAvro avro = sensorMapperToAvro.toAvro(sensorEvent);
        long timestamp = sensorEvent.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                sensorTopic,
                null,
                timestamp,
                sensorEvent.getHubId(),
                avro);

        kafkaProducer.getProducer().send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке SensorEvent в Kafka. id: {}, Error: {}",
                        sensorEvent.getId(), exception.getMessage());
                throw new RuntimeException("Ошибка отправки SensorEvent", exception);
            } else {
                log.info("Успешная отправка SensorEvent. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    public void processHub(HubAction hubAction) {
        HubEventAvro avro = hubMapperToAvro.toAvro(hubAction);
        long timestamp = hubAction.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                hubTopic,
                null,
                timestamp,
                hubAction.getHubId(),
                avro
        );

        kafkaProducer.getProducer().send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке HubEvent в Kafka. HubId: {}, Error: {}",
                        hubAction.getHubId(), exception.getMessage());
                throw new RuntimeException("Ошибка отправки HubEvent", exception);
            } else {
                log.info("Успешная отправка HubEvent. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    private void sendToKafka(ProducerRecord<String, SpecificRecordBase> record, String keyInfo) {
        kafkaProducer.getProducer().send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке события в Kafka. Key: {}, Error: {}",
                        keyInfo, exception.getMessage(), exception);
                throw new RuntimeException("Ошибка отправки события", exception);
            } else {
                log.info("Событие успешно отправлено. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
