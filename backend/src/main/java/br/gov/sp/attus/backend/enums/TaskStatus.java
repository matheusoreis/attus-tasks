package br.gov.sp.attus.backend.enums;

import java.util.Map;
import java.util.Set;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE;

    private static final Map<TaskStatus, Set<TaskStatus>> ALLOWED_TRANSITIONS =
        Map.of(
            TODO,
            Set.of(IN_PROGRESS),
            IN_PROGRESS,
            Set.of(DONE),
            DONE,
            Set.of(TODO)
        );

    public boolean canTransitionTo(TaskStatus next) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }
}
