package com.hotelfacade.facade;

import com.hotelfacade.facade.builder.ReservationBuilder;
import com.hotelfacade.facade.builder.ReservationDraft;
import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.model.Season;
import com.hotelfacade.facade.service.FeeService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationBuilderTest {

    private final LocalDate in = LocalDate.now().plusDays(10);

    @Test
    void buildsValidDraft() {
        ReservationDraft d = ReservationBuilder.builder()
                .owner("ana").guest("Ana Ruiz", "ana@mail.com")
                .roomType(RoomType.DOUBLE).stay(in, in.plusDays(3))
                .withExtra(AdditionalType.SPA, 2).build();
        assertEquals(1, d.extras().size());
        assertEquals(RoomType.DOUBLE, d.roomType());
    }

    @Test
    void rejectsCheckOutBeforeCheckIn() {
        assertThrows(IllegalArgumentException.class, () -> ReservationBuilder.builder()
                .owner("ana").guest("Ana", "ana@mail.com").roomType(RoomType.SIMPLE)
                .stay(in, in.minusDays(1)).build());
    }

    @Test
    void rejectsExtraLongerThanStay() {
        assertThrows(IllegalArgumentException.class, () -> ReservationBuilder.builder()
                .owner("ana").guest("Ana", "ana@mail.com").roomType(RoomType.SIMPLE)
                .stay(in, in.plusDays(2)).withExtra(AdditionalType.SPA, 5).build());
    }

    @Test
    void priceUsesSeasonMultiplier() {
        FeeService fees = new FeeService();
        assertEquals(new BigDecimal("360000.00"), fees.getPriceOfRoom(RoomType.SIMPLE, Season.LOW, 3));
        assertEquals(new BigDecimal("468000.00"), fees.getPriceOfRoom(RoomType.SIMPLE, Season.HIGH, 3));
    }
}
