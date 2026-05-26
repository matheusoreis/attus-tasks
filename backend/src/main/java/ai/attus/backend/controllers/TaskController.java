package ai.attus.backend.controllers;

import ai.attus.backend.dtos.TaskRequestDTO;
import ai.attus.backend.dtos.TaskResponseDTO;
import ai.attus.backend.dtos.TaskStatusDTO;
import ai.attus.backend.enums.TaskStatus;
import ai.attus.backend.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Operações de gerenciamento de tarefas")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar tarefas",
        description = "Retorna todas as tarefas. Pode ser filtrado pelo status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso"
        ),
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> findAll(
        @Parameter(
            description = "Filtrar por status: TODO, IN_PROGRESS ou DONE"
        ) @RequestParam(required = false) TaskStatus status
    ) {
        return ResponseEntity.ok(service.findAll(status));
    }

    @Operation(summary = "Buscar tarefa por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
        @ApiResponse(
            responseCode = "404",
            description = "Tarefa não encontrada",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                          "timestamp": "2024-01-15T10:30:00",
                          "status": 404,
                          "message": "Task not found with id: 1"
                        }
                    """
                )
            )
        ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(
        @Parameter(description = "ID da tarefa") @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Criar tarefa",
        description = "Cria uma nova tarefa com status inicial TODO"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Tarefa criada com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                          "timestamp": "2024-01-15T10:30:00",
                          "status": 400,
                          "errors": {
                            "title": "Title is required"
                          }
                        }
                    """
                )
            )
        ),
    })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(
        @RequestBody @Valid TaskRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            service.create(dto)
        );
    }

    @Operation(
        summary = "Atualizar tarefa",
        description = "Atualiza título e descrição de uma tarefa existente"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tarefa atualizada com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                          "timestamp": "2024-01-15T10:30:00",
                          "status": 400,
                          "errors": {
                            "title": "Title is required"
                          }
                        }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tarefa não encontrada",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                          "timestamp": "2024-01-15T10:30:00",
                          "status": 404,
                          "message": "Task not found with id: 1"
                        }
                    """
                )
            )
        ),
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(
        @Parameter(description = "ID da tarefa") @PathVariable Long id,
        @RequestBody @Valid TaskRequestDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
        summary = "Atualizar status",
        description = "Atualiza o status da tarefa respeitando as transições válidas: TODO → IN_PROGRESS → DONE"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Status atualizado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tarefa não encontrada",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                          "timestamp": "2024-01-15T10:30:00",
                          "status": 404,
                          "message": "Task not found with id: 1"
                        }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Transição de status inválida",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                          "timestamp": "2024-01-15T10:30:00",
                          "status": 422,
                          "message": "Invalid status transition from TODO to DONE"
                        }
                    """
                )
            )
        ),
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
        @Parameter(description = "ID da tarefa") @PathVariable Long id,
        @RequestBody @Valid TaskStatusDTO dto
    ) {
        return ResponseEntity.ok(service.updateStatus(id, dto.getStatus()));
    }

    @Operation(summary = "Deletar tarefa")
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Tarefa deletada com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Tarefa não encontrada",
            content = @Content(
                schema = @Schema(
                    example = """
                        {
                          "timestamp": "2024-01-15T10:30:00",
                          "status": 404,
                          "message": "Task not found with id: 1"
                        }
                    """
                )
            )
        ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID da tarefa") @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
