package com.hotelfacade.facade.model;

import java.util.List;

public enum ReservationStatus {
    RESERVED, CHECKED_IN, CHECKED_OUT, CANCELLED;

    /** Statuses that keep a room occupied. */
    public static List<ReservationStatus> activeStatuses() {
        return List.of(RESERVED, CHECKED_IN);
    }
}
