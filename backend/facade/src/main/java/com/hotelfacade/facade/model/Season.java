package com.hotelfacade.facade.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public enum Season {
    LOW("1.00"),
    HIGH("1.30");

    private final BigDecimal multiplier;

    Season(String multiplier) {
        this.multiplier = new BigDecimal(multiplier);
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    /** December, January, June and July are high season. */
    public static Season fromDate(LocalDate date) {
        int m = date.getMonthValue();
        return (m == 12 || m == 1 || m == 6 || m == 7) ? HIGH : LOW;
    }
}
