package com.hotelfacade.facade.model;

import java.math.BigDecimal;

public enum RoomType {
    SIMPLE("120000"),
    DOUBLE("180000"),
    SUITE("350000");

    private final BigDecimal basePricePerNight;

    RoomType(String basePricePerNight) {
        this.basePricePerNight = new BigDecimal(basePricePerNight);
    }

    public BigDecimal getBasePricePerNight() {
        return basePricePerNight;
    }
}
