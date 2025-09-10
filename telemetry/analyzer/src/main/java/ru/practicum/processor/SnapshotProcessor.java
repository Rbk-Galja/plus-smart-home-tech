package ru.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.service.SnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    @Value("${topic.snapshots}")
    private String snapshotsTopic;

    private final SnapshotService snapshotService;
    private final Properties snapshotConsumerProps;

    private volatile boolean running = true;
    private Consumer<String, SensorsSnapshotAvro> consumer;

    @Override
    public void run() {
        consumer = new KafkaConsumer<>(snapshotConsumerProps);
        consumer.subscribe(List.of(snapshotsTopic));

        try {
            while (running) {
                var records = consumer.poll(Duration.ofMillis(1000));
                for (var record : records) {
                    try {
                        SensorsSnapshotAvro snapshot = record.value();
                        log.info("Получение снапшота. Хаб: {}, снапшот: {}", snapshot.getHubId(), snapshot);

                        snapshotService.processSnapshot(snapshot);

                    } catch (Exception e) {
                        log.error("Ошибка при обработке снапшота: {}", record, e);
                    }
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
            log.info("Неожиданное прерывание работы SnapshotProcessor: {}", e.getMessage());
        } finally {
            consumer.close();
            log.info("Произведено корректное завершение SnapshotProcessor");
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Произведена остановка SnapshotProcessor");
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}
