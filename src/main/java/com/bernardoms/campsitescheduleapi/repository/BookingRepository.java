package com.bernardoms.campsitescheduleapi.repository;

import com.bernardoms.campsitescheduleapi.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
    @Query("select b from Booking b "
            + "where ((b.startDate < ?1 and ?2 < b.endDate) "
            + "or (?1 < b.endDate and b.endDate <= ?2) "
            + "or (?1 <= b.startDate and b.startDate <=?2)) "
            + "and b.status = 'ACTIVE' "
            + "order by b.startDate asc")
    List<Booking> findAllBookingsByDataRange(LocalDate arrivalDate, LocalDate departureDate);
}
