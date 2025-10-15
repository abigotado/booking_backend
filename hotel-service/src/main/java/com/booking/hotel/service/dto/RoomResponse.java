package com.booking.hotel.service.dto;

import com.booking.hotel.domain.enums.RoomAvailabilityStatus;

public record RoomResponse(
    Long id,
    Long hotelId,
    String number,
    RoomAvailabilityStatus availability,
    Long timesBooked
) {
}
