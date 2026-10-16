package com.hotelfacade.facade.service;

import com.hotelfacade.facade.exception.ApiException;
import com.hotelfacade.facade.model.HotelRoom;
import com.hotelfacade.facade.model.ReservationStatus;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.repository.ReservationRepository;
import com.hotelfacade.facade.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoomService {

    private final RoomRepository rooms;
    private final ReservationRepository reservations;

    public RoomService(RoomRepository rooms, ReservationRepository reservations) {
        this.rooms = rooms;
        this.reservations = reservations;
    }

    /** Rooms (optionally of one type) that are free for the whole range [from, to). */
    public List<HotelRoom> getAvailability(LocalDate from, LocalDate to, RoomType type) {
        if (from == null || to == null || !to.isAfter(from)) {
            throw new IllegalArgumentException("'to' must be after 'from'");
        }
        Set<Long> booked = new HashSet<>(
                reservations.findBookedRoomIds(from, to, ReservationStatus.activeStatuses()));
        List<HotelRoom> candidates = type == null ? rooms.findAll() : rooms.findByType(type);
        return candidates.stream().filter(r -> !booked.contains(r.getId())).toList();
    }

    public HotelRoom getRoom(Long id) {
        return rooms.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Room " + id + " not found"));
    }

    public boolean isRoomFree(Long roomId, LocalDate from, LocalDate to) {
        return !reservations.findBookedRoomIds(from, to, ReservationStatus.activeStatuses()).contains(roomId);
    }

    /** First free room of the requested type, or a 409 error when the type is sold out. */
    public HotelRoom findFreeRoom(RoomType type, LocalDate from, LocalDate to) {
        return getAvailability(from, to, type).stream().findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT,
                        "No " + type + " rooms available for those dates"));
    }
}
