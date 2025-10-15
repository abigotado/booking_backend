package com.booking.hotel.service.impl;

import com.booking.hotel.domain.entity.Hotel;
import com.booking.hotel.domain.entity.Room;
import com.booking.hotel.domain.entity.RoomLock;
import com.booking.hotel.domain.enums.RoomAvailabilityStatus;
import com.booking.hotel.mapper.RoomMapper;
import com.booking.hotel.repository.HotelRepository;
import com.booking.hotel.repository.RoomLockRepository;
import com.booking.hotel.repository.RoomRepository;
import com.booking.hotel.service.RoomService;
import com.booking.hotel.service.dto.RoomAvailabilityRequest;
import com.booking.hotel.service.dto.RoomAvailabilityResponse;
import com.booking.hotel.service.dto.RoomRecommendationResponse;
import com.booking.hotel.service.dto.RoomRequest;
import com.booking.hotel.service.dto.RoomResponse;
import com.booking.shared.exception.BusinessException;
import com.booking.shared.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomLockRepository roomLockRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomRepository roomRepository,
                           HotelRepository hotelRepository,
                           RoomLockRepository roomLockRepository,
                           RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomLockRepository = roomLockRepository;
        this.roomMapper = roomMapper;
    }

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.hotelId())
            .orElseThrow(() -> new NotFoundException("Hotel not found"));
        if (roomRepository.findByHotelIdAndNumber(hotel.getId(), request.number()).isPresent()) {
            throw new BusinessException("Room number already exists", "ROOM_DUPLICATE");
        }
        Room room = roomMapper.toEntity(request);
        room.setHotel(hotel);
        roomRepository.save(room);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Room not found"));
        if (!room.getHotel().getId().equals(request.hotelId())) {
            Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
            room.setHotel(hotel);
        }
        roomMapper.updateEntity(request, room);
        roomRepository.save(room);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Room not found"));
        roomRepository.delete(room);
    }

    @Override
    public RoomResponse getRoom(Long id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Room not found"));
        return roomMapper.toResponse(room);
    }

    @Override
    public List<RoomResponse> getRooms() {
        return roomRepository.findAllAvailable().stream()
            .map(roomMapper::toResponse)
            .toList();
    }

    @Override
    public List<RoomRecommendationResponse> getRecommendedRooms() {
        return roomRepository.findRecommendedRooms().stream()
            .map(roomMapper::toRecommendation)
            .toList();
    }

    @Override
    @Transactional
    public RoomAvailabilityResponse confirmAvailability(Long roomId, RoomAvailabilityRequest request) {
        Room room = roomRepository.findAvailableRoomById(roomId)
            .orElseThrow(() -> new NotFoundException("Room not available"));

        if (!roomLockRepository.findActiveLocksForRoom(roomId, request.startDate(), request.endDate()).isEmpty()) {
            log.warn("Room {} already locked for given range", roomId);
            return new RoomAvailabilityResponse(false, roomId, room.getHotel().getId());
        }

        RoomLock lock = roomLockRepository.findByRequestId(request.requestId())
            .orElseGet(() -> RoomLock.builder()
                .room(room)
                .bookingId(request.bookingId())
                .requestId(request.requestId())
                .correlationId(request.correlationId())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .createdAt(Instant.now())
                .active(true)
                .build());
        boolean isNewLock = lock.getId() == null;
        lock.setActive(true);
        roomLockRepository.save(lock);
        if (isNewLock) {
            room.setTimesBooked(room.getTimesBooked() + 1);
            roomRepository.save(room);
        }
        log.info("Room {} locked for booking {}", roomId, request.bookingId());
        return new RoomAvailabilityResponse(true, roomId, room.getHotel().getId());
    }

    @Override
    @Transactional
    public void releaseRoom(Long roomId, RoomAvailabilityRequest request) {
        RoomLock lock = roomLockRepository.findByRequestId(request.requestId())
            .orElse(null);
        if (lock != null) {
            lock.setActive(false);
            roomLockRepository.save(lock);
            log.info("Room {} lock released for booking {}", roomId, request.bookingId());
        }
    }
}
