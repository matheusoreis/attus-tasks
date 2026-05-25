package ai.attus.backend.dtos;

import ai.attus.backend.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusDTO {

    @NotNull(message = "Status is required")
    private TaskStatus status;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
