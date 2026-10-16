package com.hotelfacade.facade.repository;

import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByOwnerOrderByIdDesc(String owner);

    /** Ids of rooms that have an active reservation overlapping [start, end). */
    @Query("select r.room.id from Reservation r "
            + "where r.status in :statuses and r.checkIn < :end and r.checkOut > :start")
    List<Long> findBookedRoomIds(@Param("start") LocalDate start,
                                 @Param("end") LocalDate end,
                                 @Param("statuses") Collection<ReservationStatus> statuses);
}
