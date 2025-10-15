package com.booking.hotel.service.dto;

import com.booking.hotel.domain.enums.RoomAvailabilityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomRequest(
    @NotNull
    Long hotelId,
    @NotBlank
    String number,
    @NotNull
    RoomAvailabilityStatus availability
) {
}
