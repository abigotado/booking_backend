package com.booking.hotel.service.dto;

public record RoomRecommendationResponse(
    Long roomId,
    Long hotelId,
    String number,
    Long timesBooked
) {
}
