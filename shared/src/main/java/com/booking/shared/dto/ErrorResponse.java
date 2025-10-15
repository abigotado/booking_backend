package com.booking.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    List<String> details
) {

    public static ErrorResponse unauthorized(String message) {
        return new ErrorResponse(Instant.now(), 401, "Unauthorized", message, null, List.of());
    }

    public static ErrorResponse forbidden(String message) {
        return new ErrorResponse(Instant.now(), 403, "Forbidden", message, null, List.of());
    }

    public static ErrorResponse fromException(int status, String error, String message, String path, List<String> details) {
        return new ErrorResponse(Instant.now(), status, error, message, path, details == null ? List.of() : List.copyOf(details));
    }
}

