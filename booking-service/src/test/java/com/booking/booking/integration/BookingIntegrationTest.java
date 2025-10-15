package com.booking.booking.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.booking.booking.BookingServiceApplication;
import com.booking.booking.mapper.BookingMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
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

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }

    static class TestConfig {

        @org.springframework.context.annotation.Bean
        public BookingMapper bookingMapper() {
            return Mappers.getMapper(BookingMapper.class);
        }
    }
}

