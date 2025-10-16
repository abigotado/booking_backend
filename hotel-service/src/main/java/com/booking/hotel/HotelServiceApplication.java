package com.booking.hotel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.booking.hotel.domain.entity.Hotel;
import com.booking.hotel.domain.entity.Room;
import com.booking.hotel.domain.enums.RoomAvailabilityStatus;
import com.booking.hotel.repository.HotelRepository;
import com.booking.hotel.repository.RoomRepository;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class HotelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelServiceApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner hotelDataSeeder(
            HotelRepository hotelRepository,
            RoomRepository roomRepository
    ) {
        return args -> {
            if (hotelRepository.count() > 0 || roomRepository.count() > 0) {
                return;
            }

            Hotel oslo = hotelRepository.save(Hotel.builder()
                    .name("Aurora Hotel Oslo")
                    .address("Karl Johans gate 15, Oslo, Norway")
                    .build());

            Hotel tallinn = hotelRepository.save(Hotel.builder()
                    .name("Baltic Breeze Tallinn")
                    .address("Viru tänav 20, Tallinn, Estonia")
                    .build());

            List<Room> rooms = List.of(
                    Room.builder()
                            .hotel(oslo)
                            .number("101")
                            .availability(RoomAvailabilityStatus.AVAILABLE)
                            .timesBooked(5L)
                            .build(),
                    Room.builder()
                            .hotel(oslo)
                            .number("102")
                            .availability(RoomAvailabilityStatus.AVAILABLE)
                            .timesBooked(2L)
                            .build(),
                    Room.builder()
                            .hotel(tallinn)
                            .number("201")
                            .availability(RoomAvailabilityStatus.AVAILABLE)
                            .timesBooked(1L)
                            .build()
            );

            roomRepository.saveAll(rooms);
        };
    }
}

