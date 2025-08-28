package ru.practicum;

import jakarta.annotation.PreDestroy;
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

    @Value("${kafka.consumer.poll.duration:1}")
    private int pollDurationSeconds;

    private final Consumer<String, SpecificRecordBase> consumer;
    private final KafkaSnapshotProducer.SnapshotProducer producer;
    private final AggregatorService aggregatorService;
    private volatile boolean running = true;

    public void start() {
        consumer.subscribe(List.of(sensorTopic));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Сработал хук на завершение JVM. Прерываю работу консьюмера.");
            stop();
        }));

            try {
                while (running) {
                    ConsumerRecords<String, SpecificRecordBase> records = consumer
                            .poll(Duration.ofSeconds(pollDurationSeconds));
                    if (!records.isEmpty()) {
                        try {
                            for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                                aggregatorService.aggregate((SensorEventAvro) record.value())
                                        .ifPresent(snapshot -> {
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
                    }
                }
            } catch (WakeupException e) {
                log.info("Неожиданное прерывание работы консьюмера");
            } catch (Exception e) {
                log.error("Ошибка во время обработки событий от датчиков", e);
            } finally {
                log.info("Производим остановку консьюмера");
                consumer.close(Duration.ofSeconds(10));
                log.info("Производим остановку продюсера");
                producer.close();
            }
    }

    @PreDestroy
    public void stop() {
        running = false;
        consumer.wakeup();
    }
}
