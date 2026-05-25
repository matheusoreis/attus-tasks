package br.gov.sp.attus.backend.repositories;

import br.gov.sp.attus.backend.enums.TaskStatus;
import br.gov.sp.attus.backend.models.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
}
