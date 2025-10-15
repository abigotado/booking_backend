package com.booking.hotel.service.impl;

import com.booking.hotel.domain.entity.Hotel;
import com.booking.hotel.mapper.HotelMapper;
import com.booking.hotel.repository.HotelRepository;
import com.booking.hotel.service.HotelService;
import com.booking.hotel.service.dto.HotelRequest;
import com.booking.hotel.service.dto.HotelResponse;
import com.booking.shared.exception.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public HotelServiceImpl(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
    }

    @Override
    @Transactional
    public HotelResponse createHotel(HotelRequest request) {
        Hotel hotel = hotelMapper.toEntity(request);
        hotelRepository.save(hotel);
        return hotelMapper.toResponse(hotel);
    }

    @Override
    @Transactional
    public HotelResponse updateHotel(Long id, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Hotel not found"));
        hotelMapper.updateEntity(request, hotel);
        hotelRepository.save(hotel);
        return hotelMapper.toResponse(hotel);
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Hotel not found"));
        hotelRepository.delete(hotel);
    }

    @Override
    public HotelResponse getHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Hotel not found"));
        return hotelMapper.toResponse(hotel);
    }

    @Override
    public List<HotelResponse> getHotels() {
        return hotelRepository.findAll().stream()
            .map(hotelMapper::toResponse)
            .toList();
    }
}
