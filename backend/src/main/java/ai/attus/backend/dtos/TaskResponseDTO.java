package ai.attus.backend.dtos;

import ai.attus.backend.enums.TaskStatus;
import ai.attus.backend.models.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados retornados de uma tarefa")
public class TaskResponseDTO {

    @Schema(description = "ID único da tarefa", example = "1")
    private Long id;

    @Schema(
        description = "Título da tarefa",
        example = "Implementar autenticação"
    )
    private String title;

    @Schema(
        description = "Descrição da tarefa",
        example = "Usar JWT com Spring Security"
    )
    private String description;

    @Schema(description = "Status atual da tarefa", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(
        description = "Data e hora de criação",
        example = "2024-01-15T10:30:00"
    )
    private LocalDateTime createdAt;

    @Schema(
        description = "Data e hora da última atualização",
        example = "2024-01-15T14:00:00"
    )
    private LocalDateTime updatedAt;

    public static TaskResponseDTO from(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.id = task.getId();
        dto.title = task.getTitle();
        dto.description = task.getDescription();
        dto.status = task.getStatus();
        dto.createdAt = task.getCreatedAt();
        dto.updatedAt = task.getUpdatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
