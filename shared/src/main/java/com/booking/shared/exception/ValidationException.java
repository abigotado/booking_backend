package com.booking.shared.exception;

import java.util.List;

public class ValidationException extends BusinessException {

    private final transient List<String> violations;

    public ValidationException(String message, List<String> violations) {
        super(message, "VALIDATION_ERROR");
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}

