package ai.attus.backend.dtos;

import ai.attus.backend.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação ou atualização de uma tarefa")
public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must have at most 100 characters")
    @Schema(
        description = "Título da tarefa",
        example = "Implementar autenticação",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;

    @Size(max = 500, message = "Description must have at most 500 characters")
    @Schema(
        description = "Descrição detalhada da tarefa",
        example = "Usar JWT com Spring Security"
    )
    private String description;

    @Schema(
        description = "Status inicial da tarefa. Se omitido, será TODO por padrão.",
        example = "TODO"
    )
    private TaskStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
