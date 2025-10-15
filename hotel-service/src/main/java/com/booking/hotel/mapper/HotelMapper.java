package com.booking.hotel.mapper;

import com.booking.hotel.domain.entity.Hotel;
import com.booking.hotel.service.dto.HotelRequest;
import com.booking.hotel.service.dto.HotelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    Hotel toEntity(HotelRequest request);

    HotelResponse toResponse(Hotel hotel);

    void updateEntity(HotelRequest request, @MappingTarget Hotel hotel);
}
