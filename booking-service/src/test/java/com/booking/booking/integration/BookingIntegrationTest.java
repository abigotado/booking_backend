package com.booking.booking.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.booking.booking.BookingServiceApplication;
import com.booking.booking.domain.entity.User;
import com.booking.booking.domain.enums.UserRole;
import com.booking.booking.mapper.BookingMapper;
import com.booking.booking.repository.UserRepository;
import com.booking.booking.service.BookingService;
import com.booking.booking.service.dto.BookingRequest;
import com.booking.shared.exception.BusinessException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BookingServiceApplication.class)
@ActiveProfiles("test")
@Import(BookingIntegrationTest.TestConfig.class)
class BookingIntegrationTest {

    @MockBean
    private com.booking.booking.client.HotelClient hotelClient;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.save(User.builder()
            .username("date-check-user")
            .password("encoded")
            .role(UserRole.USER)
            .build());
    }

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }

    @Test
    void createBooking_invalidDateRange_shouldFail() {
        Long userId = userRepository.findAll().get(0).getId();
        BookingRequest request = new BookingRequest(1L, 1L,
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(4), false);

        assertThatThrownBy(() -> bookingService.createBooking(userId, request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("End date must be after start date");
    }

    static class TestConfig {

        @org.springframework.context.annotation.Bean
        public BookingMapper bookingMapper() {
            return Mappers.getMapper(BookingMapper.class);
        }
    }
}

