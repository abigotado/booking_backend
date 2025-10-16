package com.booking.booking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.booking.booking.domain.entity.Booking;
import com.booking.booking.domain.entity.User;
import com.booking.booking.domain.enums.BookingStatus;
import com.booking.booking.domain.enums.UserRole;
import com.booking.booking.repository.BookingRepository;
import com.booking.booking.repository.UserRepository;

@SpringBootApplication(scanBasePackages = "com.booking")
@EnableDiscoveryClient
@EnableFeignClients
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner bookingDataSeeder(
            UserRepository userRepository,
            BookingRepository bookingRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0 || bookingRepository.count() > 0) {
                return;
            }

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .build();

            User user = User.builder()
                    .username("demo")
                    .password(passwordEncoder.encode("demo123"))
                    .role(UserRole.USER)
                    .build();

            userRepository.save(admin);
            userRepository.save(user);

            Booking booking = Booking.builder()
                    .user(user)
                    .hotelId(1L)
                    .roomId(1L)
                    .startDate(LocalDate.now().plusDays(3))
                    .endDate(LocalDate.now().plusDays(5))
                    .status(BookingStatus.CONFIRMED)
                    .requestId(UUID.randomUUID().toString())
                    .correlationId(UUID.randomUUID().toString())
                    .createdAt(Instant.now())
                    .build();

            bookingRepository.save(booking);
        };
    }
}

