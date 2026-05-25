package ai.attus.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.attus.backend.dtos.TaskRequestDTO;
import ai.attus.backend.dtos.TaskResponseDTO;
import ai.attus.backend.enums.TaskStatus;
import ai.attus.backend.exceptions.InvalidStatusTransitionException;
import ai.attus.backend.exceptions.TaskNotFoundException;
import ai.attus.backend.models.Task;
import ai.attus.backend.repositories.TaskRepository;
import ai.attus.backend.services.TaskService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private Task task;
    private TaskRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.TODO);

        requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Test Task");
        requestDTO.setDescription("Test Description");
    }

    @Test
    @DisplayName("Should create task with TODO status by default")
    void create_ShouldReturnCreatedTask() {
        when(repository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = service.create(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return all tasks when no status filter")
    void findAll_ShouldReturnAllTasks() {
        when(repository.findAll()).thenReturn(List.of(task));

        List<TaskResponseDTO> response = service.findAll(null);

        assertThat(response).hasSize(1);
        verify(repository, times(1)).findAll();
        verify(repository, never()).findByStatus(any());
    }

    @Test
    @DisplayName("Should return filtered tasks when status is provided")
    void findAll_WithStatusFilter_ShouldReturnFilteredTasks() {
        when(repository.findByStatus(TaskStatus.TODO)).thenReturn(
            List.of(task)
        );

        List<TaskResponseDTO> response = service.findAll(TaskStatus.TODO);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getStatus()).isEqualTo(TaskStatus.TODO);
        verify(repository, times(1)).findByStatus(TaskStatus.TODO);
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("Should return task when ID exists")
    void findById_ShouldReturnTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDTO response = service.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when ID does not exist")
    void findById_ShouldThrowException_WhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should update title and description successfully")
    void update_ShouldUpdateFields() {
        TaskRequestDTO updateDTO = new TaskRequestDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = service.update(1L, updateDTO);

        assertThat(response).isNotNull();
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName(
        "Should throw TaskNotFoundException on update when ID does not exist"
    )
    void update_ShouldThrowException_WhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, requestDTO))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should delete task successfully")
    void delete_ShouldCallRepository() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName(
        "Should throw TaskNotFoundException on delete when ID does not exist"
    )
    void delete_ShouldThrowException_WhenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should transition from TODO to IN_PROGRESS")
    void updateStatus_TODO_to_INPROGRESS_ShouldSucceed() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = service.updateStatus(
            1L,
            TaskStatus.IN_PROGRESS
        );

        assertThat(response).isNotNull();
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should transition from IN_PROGRESS to DONE")
    void updateStatus_INPROGRESS_to_DONE_ShouldSucceed() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = service.updateStatus(1L, TaskStatus.DONE);

        assertThat(response).isNotNull();
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should reopen task from DONE to TODO")
    void updateStatus_DONE_to_TODO_ShouldSucceed() {
        task.setStatus(TaskStatus.DONE);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = service.updateStatus(1L, TaskStatus.TODO);

        assertThat(response).isNotNull();
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when transitioning TODO to DONE")
    void updateStatus_TODO_to_DONE_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.updateStatus(1L, TaskStatus.DONE))
            .isInstanceOf(InvalidStatusTransitionException.class)
            .hasMessageContaining("TODO")
            .hasMessageContaining("DONE");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName(
        "Should throw exception when transitioning IN_PROGRESS to TODO"
    )
    void updateStatus_INPROGRESS_to_TODO_ShouldThrowException() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.updateStatus(1L, TaskStatus.TODO))
            .isInstanceOf(InvalidStatusTransitionException.class)
            .hasMessageContaining("IN_PROGRESS")
            .hasMessageContaining("TODO");

        verify(repository, never()).save(any());
    }
}
