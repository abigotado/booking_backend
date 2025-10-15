package com.booking.hotel.service.dto;

public record RoomAvailabilityResponse(
    boolean available,
    Long roomId,
    Long hotelId
) {
}
