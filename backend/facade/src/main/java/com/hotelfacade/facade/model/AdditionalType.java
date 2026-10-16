package com.hotelfacade.facade.model;

import java.math.BigDecimal;

public enum AdditionalType {
    SPA("80000"),
    BREAKFAST("25000"),
    TRANSFER("60000");

    private final BigDecimal pricePerDay;

    AdditionalType(String pricePerDay) {
        this.pricePerDay = new BigDecimal(pricePerDay);
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }
}
