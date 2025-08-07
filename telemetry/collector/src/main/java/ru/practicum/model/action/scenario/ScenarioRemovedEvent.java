package ru.practicum.model.action.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.action.HubAction;
import ru.practicum.model.action.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubAction {
    @NotBlank(message = "Указано пустое название сценария для удаления")
    @Size(min = 3, message = "Название сценария не должно быть короче 3 символов")
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
