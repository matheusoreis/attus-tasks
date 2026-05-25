package br.gov.sp.attus.backend.services;

import br.gov.sp.attus.backend.dtos.TaskRequestDTO;
import br.gov.sp.attus.backend.dtos.TaskResponseDTO;
import br.gov.sp.attus.backend.enums.TaskStatus;
import br.gov.sp.attus.backend.exceptions.TaskNotFoundException;
import br.gov.sp.attus.backend.models.Task;
import br.gov.sp.attus.backend.repositories.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(
        TaskService.class
    );

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskResponseDTO> findAll(TaskStatus status) {
        log.info("Fetching tasks with status filter: {}", status);

        List<Task> tasks = (status != null)
            ? repository.findByStatus(status)
            : repository.findAll();

        return tasks
            .stream()
            .map(TaskResponseDTO::from)
            .collect(Collectors.toList());
    }

    public TaskResponseDTO findById(Long id) {
        log.info("Fetching task with id: {}", id);

        Task task = repository.findById(id).orElseThrow(() -> {
            log.warn("Task not found with id: {}", id);
            return new TaskNotFoundException(id);
        });

        return TaskResponseDTO.from(task);
    }

    public TaskResponseDTO create(TaskRequestDTO dto) {
        log.info("Creating task with title: {}", dto.getTitle());

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());

        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }

        Task saved = repository.save(task);
        log.info("Task created with id: {}", saved.getId());

        return TaskResponseDTO.from(saved);
    }

    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        log.info("Updating task with id: {}", id);

        Task task = repository.findById(id).orElseThrow(() -> {
            log.warn("Task not found for update with id: {}", id);
            return new TaskNotFoundException(id);
        });

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());

        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }

        Task updated = repository.save(task);
        log.info("Task updated successfully with id: {}", updated.getId());

        return TaskResponseDTO.from(updated);
    }

    public void delete(Long id) {
        log.info("Deleting task with id: {}", id);

        if (!repository.existsById(id)) {
            log.warn("Task not found for deletion with id: {}", id);
            throw new TaskNotFoundException(id);
        }

        repository.deleteById(id);
        log.info("Task deleted successfully with id: {}", id);
    }
}
