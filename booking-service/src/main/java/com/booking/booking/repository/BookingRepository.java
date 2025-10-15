package com.booking.booking.repository;

import com.booking.booking.domain.entity.Booking;
import com.booking.booking.domain.enums.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    Optional<Booking> findByCorrelationId(String correlationId);

    Optional<Booking> findByRequestId(String requestId);

    @Query("select count(b) > 0 from Booking b where b.roomId = :roomId and b.status = 'CONFIRMED' and b.startDate < :endDate and b.endDate > :startDate")
    boolean existsActiveBookingInRange(@Param("roomId") Long roomId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    long countByRoomIdAndStatus(Long roomId, BookingStatus status);
}
