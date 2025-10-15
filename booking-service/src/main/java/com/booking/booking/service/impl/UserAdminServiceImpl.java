package com.booking.booking.service.impl;

import com.booking.booking.domain.entity.User;
import com.booking.booking.repository.UserRepository;
import com.booking.booking.service.UserAdminService;
import com.booking.booking.service.dto.UserRegistrationRequest;
import com.booking.booking.service.dto.UserResponse;
import com.booking.booking.service.dto.UserUpdateRequest;
import com.booking.shared.exception.NotFoundException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRegistrationRequest request) {
        User user = User.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .role(com.booking.booking.domain.enums.UserRole.USER)
            .build();
        userRepository.save(user);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = getUser(userId);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        userRepository.save(user);
        return toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    @Override
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public UserResponse getByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(this::toResponse)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}

