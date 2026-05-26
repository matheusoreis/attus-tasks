package ai.attus.backend.dtos;

import ai.attus.backend.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload para atualização de status de uma tarefa")
public class TaskStatusDTO {

    @NotNull(message = "Status is required")
    @Schema(
        description = "Novo status da tarefa. Transições válidas: TODO → IN_PROGRESS → DONE",
        example = "IN_PROGRESS",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TaskStatus status;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
