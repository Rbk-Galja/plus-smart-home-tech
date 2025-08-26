package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
public class HubEventTransactionalService {
    private final HubEventService hubEventService;

    @Transactional
    public void handleEventTransactional(HubEventAvro event) {
        hubEventService.handleEvent(event);
    }
}
