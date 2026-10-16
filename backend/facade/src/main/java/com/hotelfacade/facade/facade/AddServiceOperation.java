package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.service.AdditionalChargeService;
import com.hotelfacade.facade.service.ReservationService;
import org.springframework.stereotype.Component;

/** Subsystems used: ReservationService + AdditionalChargeService. */
@Component
public class AddServiceOperation {

    private final ReservationService reservations;
    private final AdditionalChargeService extras;

    public AddServiceOperation(ReservationService reservations, AdditionalChargeService extras) {
        this.reservations = reservations;
        this.extras = extras;
    }

    public Reservation execute(Long reservationId, String owner, AdditionalType type, int days) {
        Reservation r = reservations.findOwned(reservationId, owner);
        extras.addService(r, type, days);
        return reservations.save(r);
    }
}
