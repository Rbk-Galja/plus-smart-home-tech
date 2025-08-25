package ru.practicum;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaSnapshotProducer;
import ru.practicum.service.AggregatorService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    @Value("${telemetry.sensor.topic}")
    private String sensorTopic;

    @Value("${telemetry.snapshot.topic}")
    private String sensorSnapshotTopic;

    private final Consumer<String, SpecificRecordBase> consumer;
    private final KafkaSnapshotProducer.SnapshotProducer producer;
    private final AggregatorService aggregatorService;

    @PostConstruct
    public void start() {
        consumer.subscribe(List.of(sensorTopic));

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(1));
                    if (!records.isEmpty()) {
                        try {
                            for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                                aggregatorService.aggregate((SensorEventAvro) record.value()).ifPresent(snapshot -> {
                                    producer.getProducer().send(new ProducerRecord<>(
                                            sensorSnapshotTopic,
                                            null,
                                            snapshot.getTimestamp().toEpochMilli(),
                                            snapshot.getHubId(),
                                            snapshot
                                    ));
                                });

                            }
                            consumer.commitSync();

                        } catch (Exception e) {
                            log.error("Ошибка обработки батча сообщений", e);
                        }
                    }}
            } catch (WakeupException ignored) {
                log.info("Неожиданное прерывание работы консьюмера");
            } catch (Exception e) {
                log.error("Ошибка во время обработки событий от датчиков", e);
            } finally {
                    log.info("Производим остановку консьюмер");
                    consumer.close(Duration.ofSeconds(10));
                    log.info("Производим остановку продюсера");
                    producer.close();
                }
        }, "aggregation-thread");

        thread.start();
    }
            }

