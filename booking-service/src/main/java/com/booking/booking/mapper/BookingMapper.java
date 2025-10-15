package com.booking.booking.mapper;

import com.booking.booking.domain.entity.Booking;
import com.booking.booking.service.dto.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "userId", source = "user.id")
    BookingResponse toResponse(Booking booking);
}
