package com.booking.booking.service.dto;

import com.booking.booking.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
    @NotBlank
    String password,
    @NotNull
    UserRole role
) {
}
