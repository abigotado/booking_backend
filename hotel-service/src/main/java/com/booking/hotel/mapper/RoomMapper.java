package com.booking.hotel.mapper;

import com.booking.hotel.domain.entity.Room;
import com.booking.hotel.service.dto.RoomRecommendationResponse;
import com.booking.hotel.service.dto.RoomRequest;
import com.booking.hotel.service.dto.RoomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timesBooked", constant = "0L")
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "version", ignore = true)
    Room toEntity(RoomRequest request);

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomResponse toResponse(Room room);

    @Mapping(target = "roomId", source = "id")
    @Mapping(target = "hotelId", source = "hotel.id")
    RoomRecommendationResponse toRecommendation(Room room);

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timesBooked", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(RoomRequest request, @MappingTarget Room room);
}
