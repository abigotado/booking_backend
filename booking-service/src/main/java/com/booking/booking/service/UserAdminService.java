package com.booking.booking.service;

import com.booking.booking.service.dto.UserRegistrationRequest;
import com.booking.booking.service.dto.UserResponse;
import com.booking.booking.service.dto.UserUpdateRequest;
import java.util.List;

public interface UserAdminService {

    UserResponse createUser(UserRegistrationRequest request);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    void deleteUser(Long userId);

    List<UserResponse> listUsers();

    UserResponse getByUsername(String username);
}
