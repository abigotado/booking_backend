package com.booking.booking.service.dto;

import com.booking.booking.domain.enums.BookingStatus;
import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
    Long id,
    Long userId,
    Long hotelId,
    Long roomId,
    LocalDate startDate,
    LocalDate endDate,
    BookingStatus status,
    Instant createdAt
) {
}
