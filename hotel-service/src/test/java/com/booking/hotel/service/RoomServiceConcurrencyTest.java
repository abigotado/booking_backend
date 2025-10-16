package com.booking.hotel.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.booking.hotel.HotelServiceApplication;
import com.booking.hotel.domain.entity.Hotel;
import com.booking.hotel.domain.entity.Room;
import com.booking.hotel.domain.entity.RoomLock;
import com.booking.hotel.domain.enums.RoomAvailabilityStatus;
import com.booking.hotel.repository.HotelRepository;
import com.booking.hotel.repository.RoomLockRepository;
import com.booking.hotel.repository.RoomRepository;
import com.booking.hotel.service.dto.RoomAvailabilityRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = HotelServiceApplication.class)
@ActiveProfiles("test")
class RoomServiceConcurrencyTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomLockRepository roomLockRepository;

    private Room room;

    @BeforeEach
    void setup() {
        roomLockRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();

        Hotel hotel = hotelRepository.save(Hotel.builder()
            .name("Concurrency Hotel")
            .address("Concurrency street")
            .build());

        room = roomRepository.save(Room.builder()
            .hotel(hotel)
            .number("C101")
            .availability(RoomAvailabilityStatus.AVAILABLE)
            .timesBooked(0L)
            .build());
    }

    @Test
    void concurrentLockRequests_onlyOneActiveLock() throws InterruptedException {
        var executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);
        List<Boolean> responses = new CopyOnWriteArrayList<>();

        Runnable confirmTask = () -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                RoomAvailabilityRequest request = new RoomAvailabilityRequest(
                    UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString());
                responses.add(roomService.confirmAvailability(room.getId(), request).available());
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(confirmTask);
        executor.submit(confirmTask);

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdownNow();

        long successes = responses.stream().filter(Boolean::booleanValue).count();
        List<RoomLock> locks = roomLockRepository.findAll();
        long activeLocks = locks.stream().filter(RoomLock::isActive).count();

        assertThat(successes).isEqualTo(1);
        assertThat(activeLocks).isEqualTo(1);
        assertThat(roomRepository.findById(room.getId()).orElseThrow().getTimesBooked()).isEqualTo(1L);
    }
} 
