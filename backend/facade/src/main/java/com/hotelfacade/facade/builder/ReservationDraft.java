package com.hotelfacade.facade.builder;

import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.model.Season;

import java.time.LocalDate;
import java.util.List;

/** Immutable product of {@link ReservationBuilder}. */
public record ReservationDraft(
        String guestName,
        String guestEmail,
        String owner,
        RoomType roomType,
        LocalDate checkIn,
        LocalDate checkOut,
        Season season,
        List<Extra> extras) {

    public record Extra(AdditionalType type, int days) {
    }
}
