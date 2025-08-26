package ru.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.service.HubEventTransactionalService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    @Value("${topic.hub-events}")
    private String hubEventsTopic;

    private final HubEventTransactionalService hubEventTransactionalService;
    private final Properties hubConsumerProps;

    private volatile boolean running = true;
    private Consumer<String, HubEventAvro> consumer;

    @Override
    public void run() {
        consumer = new KafkaConsumer<>(hubConsumerProps);
        consumer.subscribe(List.of(hubEventsTopic));

        try {
            while (running) {
                var records = consumer.poll(Duration.ofMillis(1000));

                records.forEach(record -> {
                    HubEventAvro event = record.value();
                    log.info("Получение события. Хаб: {}, событие: {}", event.getHubId(), event);

                    hubEventTransactionalService.handleEventTransactional(event);
                });
            }
        } catch (WakeupException e) {
            log.info("Неожиданное прерывание работы HubEventProcessor. {}", e.getMessage());
        } finally {
            consumer.close();
            log.info("HubEventProcessor корректно завершён");
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Произведена остановка HubEventProcessor");
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}
