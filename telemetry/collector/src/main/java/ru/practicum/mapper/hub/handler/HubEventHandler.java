package ru.practicum.mapper.hub.handler;

import ru.practicum.model.action.HubAction;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {
    HubEventProto.PayloadCase getMessageType();
    HubAction toJava(HubEventProto protoEvent);
}
