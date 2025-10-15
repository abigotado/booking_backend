package com.booking.booking.service;

import com.booking.booking.service.dto.AuthRequest;
import com.booking.booking.service.dto.AuthResponse;
import com.booking.booking.service.dto.UserRegistrationRequest;

public interface UserService {

    AuthResponse register(UserRegistrationRequest request);

    AuthResponse authenticate(AuthRequest request);
}
