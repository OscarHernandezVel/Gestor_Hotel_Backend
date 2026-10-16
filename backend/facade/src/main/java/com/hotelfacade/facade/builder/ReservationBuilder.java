package com.hotelfacade.facade.builder;

import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.model.Season;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BUILDER pattern: assembles a valid {@link ReservationDraft} step by step
 * and validates everything once, in {@link #build()}.
 */
public class ReservationBuilder {

    private String guestName;
    private String guestEmail;
    private String owner;
    private RoomType roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Season season;
    private final List<ReservationDraft.Extra> extras = new ArrayList<>();

    public static ReservationBuilder builder() {
        return new ReservationBuilder();
    }

    public ReservationBuilder guest(String name, String email) {
        this.guestName = name;
        this.guestEmail = email;
        return this;
    }

    public ReservationBuilder owner(String username) {
        this.owner = username;
        return this;
    }

    public ReservationBuilder roomType(RoomType roomType) {
        this.roomType = roomType;
        return this;
    }

    public ReservationBuilder stay(LocalDate checkIn, LocalDate checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        return this;
    }

    /** Optional. If omitted, the season is derived from the check-in date. */
    public ReservationBuilder season(Season season) {
        this.season = season;
        return this;
    }

    public ReservationBuilder withExtra(AdditionalType type, int days) {
        this.extras.add(new ReservationDraft.Extra(type, days));
        return this;
    }

    public ReservationDraft build() {
        require(guestName != null && !guestName.isBlank(), "Guest name is required");
        require(guestEmail != null && guestEmail.contains("@"), "A valid guest email is required");
        require(owner != null && !owner.isBlank(), "Reservation owner is required");
        require(roomType != null, "Room type is required");
        require(checkIn != null && checkOut != null, "Check-in and check-out dates are required");
        require(!checkIn.isBefore(LocalDate.now()), "Check-in cannot be in the past");
        require(checkOut.isAfter(checkIn), "Check-out must be after check-in");

        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        for (ReservationDraft.Extra e : extras) {
            require(e.type() != null, "Extra service type is required");
            require(e.days() >= 1 && e.days() <= nights,
                    "Extra service days must be between 1 and the number of nights (" + nights + ")");
        }
        Season finalSeason = season != null ? season : Season.fromDate(checkIn);
        return new ReservationDraft(guestName.trim(), guestEmail.trim(), owner, roomType,
                checkIn, checkOut, finalSeason, List.copyOf(extras));
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
