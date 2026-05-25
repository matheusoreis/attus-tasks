package br.gov.sp.attus.backend.dtos;

import br.gov.sp.attus.backend.enums.TaskStatus;
import br.gov.sp.attus.backend.models.Task;
import java.time.LocalDateTime;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
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
