package com.booking.hotel.service;

import com.booking.hotel.service.dto.RoomAvailabilityRequest;
import com.booking.hotel.service.dto.RoomAvailabilityResponse;
import com.booking.hotel.service.dto.RoomRecommendationResponse;
import com.booking.hotel.service.dto.RoomRequest;
import com.booking.hotel.service.dto.RoomResponse;
import java.util.List;

public interface RoomService {

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long id, RoomRequest request);

    void deleteRoom(Long id);

    RoomResponse getRoom(Long id);

    List<RoomResponse> getRooms();

    List<RoomRecommendationResponse> getRecommendedRooms();

    RoomAvailabilityResponse confirmAvailability(Long roomId, RoomAvailabilityRequest request);

    void releaseRoom(Long roomId, RoomAvailabilityRequest request);
}
