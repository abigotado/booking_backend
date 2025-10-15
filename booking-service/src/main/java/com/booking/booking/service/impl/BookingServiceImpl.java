package com.booking.booking.service.impl;

import com.booking.booking.client.HotelClient;
import com.booking.booking.domain.entity.Booking;
import com.booking.booking.domain.entity.User;
import com.booking.booking.domain.enums.BookingStatus;
import com.booking.booking.mapper.BookingMapper;
import com.booking.booking.repository.BookingRepository;
import com.booking.booking.repository.UserRepository;
import com.booking.booking.service.BookingService;
import com.booking.booking.service.dto.BookingRequest;
import com.booking.booking.service.dto.BookingResponse;
import com.booking.booking.service.dto.RoomAvailabilityRequest;
import com.booking.booking.service.dto.RoomAvailabilityResponse;
import com.booking.booking.service.dto.RoomRecommendationResponse;
import com.booking.shared.exception.BusinessException;
import com.booking.shared.exception.NotFoundException;
import com.booking.shared.logging.CorrelationContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private static final String HOTEL_SERVICE_CB = "hotelService";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelClient hotelClient;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              HotelClient hotelClient,
                              BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.hotelClient = hotelClient;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(Long userId, @Valid BookingRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        validateDates(request.startDate(), request.endDate());

        Long correlationBookingId = bookingRepository.findByRequestId(requestHash(userId, request))
            .map(Booking::getId)
            .orElse(null);
        if (correlationBookingId != null) {
            log.info("Idempotent booking request detected for user {} returning existing booking {}", userId, correlationBookingId);
            Booking booking = bookingRepository.findById(correlationBookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
            return bookingMapper.toResponse(booking);
        }

        String correlationId = CorrelationContext.getCorrelationId();
        String requestId = requestHash(userId, request);
        Long roomId = resolveRoomId(request);

        if (bookingRepository.existsActiveBookingInRange(roomId, request.startDate(), request.endDate())) {
            throw new BusinessException("Room already booked in the given period", "ROOM_OCCUPIED");
        }

        Booking booking = Booking.builder()
            .user(user)
            .roomId(roomId)
            .hotelId(request.hotelId())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .status(BookingStatus.PENDING)
            .createdAt(Instant.now())
            .correlationId(correlationId)
            .requestId(requestId)
            .build();
        bookingRepository.saveAndFlush(booking);

        try {
            RoomAvailabilityResponse response = confirmRoomAvailability(roomId, request, booking);
            if (!response.available()) {
                throw new BusinessException("Room not available", "ROOM_NOT_AVAILABLE");
            }
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            log.info("Booking {} confirmed", booking.getId());
            return bookingMapper.toResponse(booking);
        } catch (Exception ex) {
            log.error("Failed to confirm booking {}. Triggering compensation.", booking.getId(), ex);
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            releaseRoom(roomId, request, booking);
            throw ex;
        }
    }

    @Override
    public BookingResponse getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException("Cannot access booking", "ACCESS_DENIED");
        }
        return bookingMapper.toResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
            .map(bookingMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public void cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new BusinessException("Cannot cancel booking", "ACCESS_DENIED");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        releaseRoom(booking.getRoomId(),
            new BookingRequest(booking.getHotelId(), booking.getRoomId(), booking.getStartDate(), booking.getEndDate(), false),
            booking);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new BusinessException("End date must be after start date", "INVALID_DATES");
        }
    }

    private String requestHash(Long userId, BookingRequest request) {
        return UUID.nameUUIDFromBytes((userId + ":" + request.hotelId() + ":" + request.roomId() + ":" +
            request.startDate() + ":" + request.endDate()).getBytes()).toString();
    }

    private Long resolveRoomId(BookingRequest request) {
        if (!request.autoSelect()) {
            if (request.roomId() == null) {
                throw new BusinessException("roomId required when autoSelect is false", "ROOM_REQUIRED");
            }
            return request.roomId();
        }
        List<RoomRecommendationResponse> recommendations = hotelClient.getRecommendedRooms();
        return recommendations.stream()
            .filter(room -> room.hotelId().equals(request.hotelId()))
            .map(RoomRecommendationResponse::roomId)
            .findFirst()
            .orElseThrow(() -> new BusinessException("No rooms available for auto-selection", "NO_AVAILABLE_ROOMS"));
    }

    @Retry(name = HOTEL_SERVICE_CB, fallbackMethod = "availabilityFallback")
    @CircuitBreaker(name = HOTEL_SERVICE_CB, fallbackMethod = "availabilityFallback")
    protected RoomAvailabilityResponse confirmRoomAvailability(Long roomId, BookingRequest request, Booking booking) {
        return hotelClient.confirmAvailability(roomId,
            new RoomAvailabilityRequest(booking.getId(), request.startDate(), request.endDate(), booking.getCorrelationId(), booking.getRequestId()));
    }

    protected RoomAvailabilityResponse availabilityFallback(Long roomId, BookingRequest request, Booking booking, Throwable throwable) {
        log.warn("Hotel service unavailable for booking {}. Triggering cancellation.", booking.getId());
        throw new BusinessException("Hotel service unavailable", "HOTEL_SERVICE_UNAVAILABLE");
    }

    @Retry(name = HOTEL_SERVICE_CB)
    @CircuitBreaker(name = HOTEL_SERVICE_CB)
    protected void releaseRoom(Long roomId, BookingRequest request, Booking booking) {
        hotelClient.releaseRoom(roomId,
            new RoomAvailabilityRequest(booking.getId(), request.startDate(), request.endDate(), booking.getCorrelationId(), booking.getRequestId()));
    }
}
