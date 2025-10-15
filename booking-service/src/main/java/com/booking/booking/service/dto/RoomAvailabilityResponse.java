package com.booking.booking.service.dto;

public record RoomAvailabilityResponse(
    boolean available,
    Long roomId,
    Long hotelId
) {
}
