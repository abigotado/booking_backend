package com.booking.hotel.service;

import com.booking.hotel.service.dto.HotelRequest;
import com.booking.hotel.service.dto.HotelResponse;
import java.util.List;

public interface HotelService {

    HotelResponse createHotel(HotelRequest request);

    HotelResponse updateHotel(Long id, HotelRequest request);

    void deleteHotel(Long id);

    HotelResponse getHotel(Long id);

    List<HotelResponse> getHotels();
}
