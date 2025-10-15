package com.booking.booking.service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookingRequest(
    @NotNull
    Long hotelId,
    Long roomId,
    @NotNull
    @Future
    LocalDate startDate,
    @NotNull
    @Future
    LocalDate endDate,
    boolean autoSelect
) {
}
