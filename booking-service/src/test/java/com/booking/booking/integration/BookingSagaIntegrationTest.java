package com.booking.booking.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.booking.booking.BookingServiceApplication;
import com.booking.booking.client.HotelClient;
import com.booking.booking.domain.entity.User;
import com.booking.booking.domain.enums.UserRole;
import com.booking.booking.repository.BookingRepository;
import com.booking.booking.repository.UserRepository;
import com.booking.booking.service.BookingService;
import com.booking.booking.service.dto.BookingRequest;
import com.booking.shared.security.JwtTokenService;
import java.time.LocalDate;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = BookingServiceApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.cloud.compatibility-verifier.enabled=false")
class BookingSagaIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @MockBean
    private HotelClient hotelClient;

    private Long userId;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
            .username("test-user")
            .password("encoded")
            .role(UserRole.USER)
            .build();
        userRepository.save(user);
        userId = user.getId();
    }

    @Test
    void createBooking_successfulSaga() {
        given(hotelClient.confirmAvailability(eq(1L), any()))
            .willReturn(new com.booking.booking.service.dto.RoomAvailabilityResponse(true, 1L, 1L));

        BookingRequest request = new BookingRequest(1L, 1L, LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2), false);

        bookingService.createBooking(userId, request);

        assertThat(bookingRepository.findAll()).hasSize(1);
        assertThat(bookingRepository.findAll().get(0).getStatus())
            .isEqualTo(com.booking.booking.domain.enums.BookingStatus.CONFIRMED);
    }

    @Test
    void createBooking_compensationOnFailure() {
        given(hotelClient.confirmAvailability(eq(1L), any()))
            .willThrow(new RuntimeException("Hotel service failure"));

        BookingRequest request = new BookingRequest(1L, 1L, LocalDate.now().plusDays(3),
            LocalDate.now().plusDays(4), false);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> bookingService.createBooking(userId, request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Hotel service failure");

        assertThat(bookingRepository.findAll()).isEmpty();
    }
}
