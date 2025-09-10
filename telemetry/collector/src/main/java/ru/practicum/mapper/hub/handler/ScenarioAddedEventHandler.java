package ru.practicum.mapper.hub.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mapper.hub.mapstruct.HubMapperToProto;
import ru.practicum.model.action.HubAction;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final HubMapperToProto hubMapperToProto;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubAction toJava(HubEventProto protoEvent) {
        return hubMapperToProto.scenarioAddedToJava(protoEvent);
    }
}
