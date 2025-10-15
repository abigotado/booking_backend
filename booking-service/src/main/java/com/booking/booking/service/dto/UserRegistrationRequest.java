package com.booking.booking.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
    @NotBlank
    @Size(min = 4, max = 64)
    String username,
    @NotBlank
    @Size(min = 8, max = 128)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
             message = "Password must contain upper, lower case letters and digits")
    String password
) {
}
