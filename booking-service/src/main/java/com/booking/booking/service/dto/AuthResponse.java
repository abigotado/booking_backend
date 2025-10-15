package com.booking.booking.service.dto;

import com.booking.booking.domain.enums.UserRole;

public record AuthResponse(
    String token,
    UserRole role
) {
}
