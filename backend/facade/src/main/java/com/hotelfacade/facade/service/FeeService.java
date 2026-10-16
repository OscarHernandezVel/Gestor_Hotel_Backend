package com.hotelfacade.facade.service;

import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.model.Season;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FeeService {

    /** Price of a stay: base price x season multiplier x nights. */
    public BigDecimal getPriceOfRoom(RoomType type, Season season, long nights) {
        return type.getBasePricePerNight()
                .multiply(season.getMultiplier())
                .multiply(BigDecimal.valueOf(nights))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPriceOfAdditional(AdditionalType type, int days) {
        return type.getPricePerDay()
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
