package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.model.ReservationStatus;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.model.Season;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReservationView(Long id, String guestName, String guestEmail, String roomNumber,
                              RoomType roomType, LocalDate checkIn, LocalDate checkOut, long nights,
                              Season season, ReservationStatus status, BigDecimal roomTotal,
                              BigDecimal extrasTotal, BigDecimal total, String digitalKey,
                              List<ExtraView> extras) {
}
