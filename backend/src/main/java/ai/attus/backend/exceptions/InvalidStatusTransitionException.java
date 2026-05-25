package ai.attus.backend.exceptions;

import ai.attus.backend.enums.TaskStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TaskStatus from, TaskStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
