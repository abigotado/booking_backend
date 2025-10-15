package com.booking.hotel.repository;

import com.booking.hotel.domain.entity.RoomLock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomLockRepository extends JpaRepository<RoomLock, Long> {

    Optional<RoomLock> findByRequestId(String requestId);

    Optional<RoomLock> findByBookingId(Long bookingId);

    @Query("select rl from RoomLock rl where rl.room.id = :roomId and rl.active = true and rl.startDate < :endDate and rl.endDate > :startDate")
    List<RoomLock> findActiveLocksForRoom(@Param("roomId") Long roomId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
