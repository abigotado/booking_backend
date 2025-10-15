package com.booking.booking.client;

import com.booking.booking.service.dto.RoomAvailabilityRequest;
import com.booking.booking.service.dto.RoomAvailabilityResponse;
import com.booking.booking.service.dto.RoomRecommendationResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "hotel-service", path = "/api")
public interface HotelClient {

    @PostMapping("/rooms/{roomId}/confirm-availability")
    RoomAvailabilityResponse confirmAvailability(@PathVariable("roomId") Long roomId,
                                                 @RequestBody RoomAvailabilityRequest request);

    @PostMapping("/rooms/{roomId}/release")
    void releaseRoom(@PathVariable("roomId") Long roomId,
                     @RequestBody RoomAvailabilityRequest request);

    @GetMapping("/rooms/recommend")
    List<RoomRecommendationResponse> getRecommendedRooms();
}
