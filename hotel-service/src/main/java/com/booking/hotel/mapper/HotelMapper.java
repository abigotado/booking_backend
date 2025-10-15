package com.booking.hotel.mapper;

import com.booking.hotel.domain.entity.Hotel;
import com.booking.hotel.service.dto.HotelRequest;
import com.booking.hotel.service.dto.HotelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "id", ignore = true)
    Hotel toEntity(HotelRequest request);

    HotelResponse toResponse(Hotel hotel);

    @Mapping(target = "id", ignore = true)
    void updateEntity(HotelRequest request, @MappingTarget Hotel hotel);
}
