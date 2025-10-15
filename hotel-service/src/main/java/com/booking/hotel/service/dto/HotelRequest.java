package com.booking.hotel.service.dto;

import jakarta.validation.constraints.NotBlank;

public record HotelRequest(
    @NotBlank
    String name,
    @NotBlank
    String address
) {
}
