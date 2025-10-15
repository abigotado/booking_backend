package com.booking.booking.service.dto;

import java.time.LocalDate;

public record RoomAvailabilityRequest(
    Long bookingId,
    LocalDate startDate,
    LocalDate endDate,
    String correlationId,
    String requestId
) {
}
