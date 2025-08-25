package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRouterClient {
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    public void sendAction(DeviceActionRequest request) {
        try {
            hubRouterStub.handleDeviceAction(request);
            log.info("Произведена отправка команды хабу: {}", request);
        } catch (Exception e) {
            log.error("Не удалось отправить команду хабу", e);
        }
    }
}
