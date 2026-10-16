package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.model.Season;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Quote(RoomType roomType, LocalDate checkIn, LocalDate checkOut, long nights,
                    Season season, BigDecimal total) {
}
