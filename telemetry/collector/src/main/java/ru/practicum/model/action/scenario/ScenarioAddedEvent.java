package ru.practicum.model.action.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.action.HubAction;
import ru.practicum.model.action.HubEventType;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubAction {
    @NotBlank(message = "Указано пустое название сценария")
    @Size(min = 3, message = "Название не должно быть короче 3 символов")
    private String name;

    @NotNull(message = "Указан пустой сптсок условий для сценария.")
    private List<ScenarioCondition> conditions;

    @NotNull(message = "Указан пустой список действий для сценария.")
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
