package com.booking.booking.controller;

import com.booking.booking.service.BookingService;
import com.booking.booking.service.dto.BookingRequest;
import com.booking.booking.service.dto.BookingResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return bookingService.createBooking(userId, request);
    }

    @GetMapping
    public List<BookingResponse> getBookings(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/{id}")
    public BookingResponse getBooking(@PathVariable Long id, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return bookingService.getBooking(userId, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable Long id, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        bookingService.cancelBooking(userId, id);
    }
}
