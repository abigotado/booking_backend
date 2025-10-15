package com.booking.hotel.repository;

import com.booking.hotel.domain.entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByHotelIdAndNumber(Long hotelId, String number);

    @Query("select r from Room r where r.availability = 'AVAILABLE'")
    List<Room> findAllAvailable();

    @Query("select r from Room r where r.availability = 'AVAILABLE' order by r.timesBooked asc, r.id asc")
    List<Room> findRecommendedRooms();

    @Query("select r from Room r where r.availability = 'AVAILABLE' and r.id = :roomId")
    Optional<Room> findAvailableRoomById(@Param("roomId") Long roomId);
}
