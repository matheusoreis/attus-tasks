package br.gov.sp.attus.backend.controllers;

import br.gov.sp.attus.backend.dtos.TaskRequestDTO;
import br.gov.sp.attus.backend.dtos.TaskResponseDTO;
import br.gov.sp.attus.backend.enums.TaskStatus;
import br.gov.sp.attus.backend.services.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> findAll(
        @RequestParam(required = false) TaskStatus status
    ) {
        return ResponseEntity.ok(service.findAll(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(
        @RequestBody @Valid TaskRequestDTO dto
    ) {
        TaskResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(
        @PathVariable Long id,
        @RequestBody @Valid TaskRequestDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
        @PathVariable Long id,
        @RequestParam TaskStatus status
    ) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}
