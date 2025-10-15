package com.booking.booking.controller;

import com.booking.booking.service.UserAdminService;
import com.booking.booking.service.UserService;
import com.booking.booking.service.dto.AuthRequest;
import com.booking.booking.service.dto.AuthResponse;
import com.booking.booking.service.dto.UserRegistrationRequest;
import com.booking.booking.service.dto.UserResponse;
import com.booking.booking.service.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserAdminService userAdminService;

    public UserController(UserService userService, UserAdminService userAdminService) {
        this.userService = userService;
        this.userAdminService = userAdminService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody UserRegistrationRequest request) {
        return userService.register(request);
    }

    @PostMapping("/auth")
    public AuthResponse authenticate(@Valid @RequestBody AuthRequest request) {
        return userService.authenticate(request);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserRegistrationRequest request) {
        return userAdminService.createUser(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userAdminService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userAdminService.deleteUser(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return userAdminService.listUsers();
    }

    @GetMapping("/me")
    public UserResponse currentUser(Principal principal) {
        return userAdminService.getByUsername(principal.getName());
    }
}

