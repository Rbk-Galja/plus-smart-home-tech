package ru.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> hubEventsConsumer;
    private volatile boolean running = true;

    @Override
    public void run() {
        hubEventsConsumer.subscribe(List.of("telemetry.hubs.v1"));

        try {
            while (running) {
                var records = hubEventsConsumer.poll(Duration.ofMillis(1000));
                records.forEach(record -> {
                    HubEventAvro event = record.value();
                    log.info("Получение события. Хаб: {}, событие: {}", event.getHubId(), event);
                });
            }
        } catch (WakeupException e) {
            log.info("Неожиданное прерывание работы HubEventProcessor: {}", e.getMessage());
        } finally {
            hubEventsConsumer.close();
            log.info("Произведено корректное завершение HubEventProcessor");
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Произведена остановка HubEventProcessor");
        running = false;
        hubEventsConsumer.wakeup();
    }
}
