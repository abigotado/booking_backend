package com.booking.booking.service.impl;

import com.booking.booking.domain.entity.User;
import com.booking.booking.domain.enums.UserRole;
import com.booking.booking.repository.UserRepository;
import com.booking.booking.service.UserService;
import com.booking.booking.service.dto.AuthRequest;
import com.booking.booking.service.dto.AuthResponse;
import com.booking.booking.service.dto.UserRegistrationRequest;
import com.booking.shared.exception.BusinessException;
import com.booking.shared.exception.NotFoundException;
import com.booking.shared.security.JwtTokenService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtTokenService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists", "USERNAME_DUPLICATE");
        }

        User user = User.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .role(UserRole.USER)
            .build();
        userRepository.save(user);
        log.info("Registered new user username={} role={}", user.getUsername(), user.getRole());

        String token = generateToken(user);
        return new AuthResponse(token, user.getRole());
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new NotFoundException("User not found"));

        String token = jwtTokenService.generateToken(authentication.getName(), getAuthorities(user));
        return new AuthResponse(token, user.getRole());
    }

    private String generateToken(User user) {
        return jwtTokenService.generateToken(user.getUsername(), getAuthorities(user));
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
