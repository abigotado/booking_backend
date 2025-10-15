package com.booking.hotel.controller;

import com.booking.hotel.service.RoomService;
import com.booking.hotel.service.dto.RoomAvailabilityRequest;
import com.booking.hotel.service.dto.RoomAvailabilityResponse;
import com.booking.hotel.service.dto.RoomRecommendationResponse;
import com.booking.hotel.service.dto.RoomRequest;
import com.booking.hotel.service.dto.RoomResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse createRoom(@Valid @RequestBody RoomRequest request) {
        return roomService.createRoom(request);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return roomService.updateRoom(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }

    @GetMapping
    public List<RoomResponse> getRooms() {
        return roomService.getRooms();
    }

    @GetMapping("/recommend")
    public List<RoomRecommendationResponse> getRecommendedRooms() {
        return roomService.getRecommendedRooms();
    }

    @PostMapping("/{roomId}/confirm-availability")
    public RoomAvailabilityResponse confirmAvailability(@PathVariable Long roomId,
                                                        @RequestBody RoomAvailabilityRequest request) {
        return roomService.confirmAvailability(roomId, request);
    }

    @PostMapping("/{roomId}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void releaseRoom(@PathVariable Long roomId,
                            @RequestBody RoomAvailabilityRequest request) {
        roomService.releaseRoom(roomId, request);
    }
}
