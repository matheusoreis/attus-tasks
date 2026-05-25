package br.gov.sp.attus.backend.exceptions;

import br.gov.sp.attus.backend.enums.TaskStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TaskStatus from, TaskStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
