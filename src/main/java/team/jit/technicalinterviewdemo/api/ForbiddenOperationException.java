package team.jit.technicalinterviewdemo.api;

import org.springframework.security.access.AccessDeniedException;

public class ForbiddenOperationException extends AccessDeniedException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
