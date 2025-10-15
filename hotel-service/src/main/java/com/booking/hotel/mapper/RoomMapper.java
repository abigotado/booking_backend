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

    @Mapping(target = "hotel", ignore = true)
    Room toEntity(RoomRequest request);

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomResponse toResponse(Room room);

    RoomRecommendationResponse toRecommendation(Room room);

    @Mapping(target = "hotel", ignore = true)
    void updateEntity(RoomRequest request, @MappingTarget Room room);
}
