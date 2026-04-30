package team.jit.technicalinterviewdemo.technical.api;

import org.springframework.security.access.AccessDeniedException;

public class ForbiddenOperationException extends AccessDeniedException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
