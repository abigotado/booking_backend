package com.booking.booking.service.dto;

import com.booking.booking.domain.enums.UserRole;

public record UserResponse(
    Long id,
    String username,
    UserRole role
) {
}
