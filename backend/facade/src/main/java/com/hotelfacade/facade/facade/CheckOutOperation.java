package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.exception.ApiException;
import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.model.ReservationStatus;
import com.hotelfacade.facade.service.BillingService;
import com.hotelfacade.facade.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Subsystems used: ReservationService + BillingService. */
@Component
public class CheckOutOperation {

    private final ReservationService reservations;
    private final BillingService billing;

    public CheckOutOperation(ReservationService reservations, BillingService billing) {
        this.reservations = reservations;
        this.billing = billing;
    }

    public Invoice execute(Long reservationId, String owner) {
        Reservation r = reservations.findOwned(reservationId, owner);
        if (r.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Only CHECKED_IN reservations can check out (current: " + r.getStatus() + ")");
        }
        r.setStatus(ReservationStatus.CHECKED_OUT);
        r.setDigitalKey(null); // the digital key stops working
        reservations.save(r);
        return billing.getBreakdown(r);
    }
}
