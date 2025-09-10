package ru.practicum.mapper.hub.mapstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.model.action.HubAction;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
@RequiredArgsConstructor
public class HubMapperToJava {
    private final HubMapperToProto hubMapperToProto;

    public HubAction toJava(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> hubMapperToProto.deviceAddedToJava(proto);
            case DEVICE_REMOVED -> hubMapperToProto.deviceRemovedToJava(proto);
            case SCENARIO_ADDED -> hubMapperToProto.scenarioAddedToJava(proto);
            case SCENARIO_REMOVED -> hubMapperToProto.scenarioRemovedToJava(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Не найден указанный тип датчика");
        };
    }
}
