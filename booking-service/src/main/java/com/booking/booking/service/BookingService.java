package com.booking.booking.service;

import com.booking.booking.service.dto.BookingRequest;
import com.booking.booking.service.dto.BookingResponse;
import java.util.List;

public interface BookingService {

    BookingResponse createBooking(Long userId, BookingRequest request);

    BookingResponse getBooking(Long userId, Long bookingId);

    List<BookingResponse> getUserBookings(Long userId);

    void cancelBooking(Long userId, Long bookingId);
}
