package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.exception.ApiException;
import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.model.ReservationStatus;
import com.hotelfacade.facade.service.AccessService;
import com.hotelfacade.facade.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Subsystems used: ReservationService + AccessService. */
@Component
public class CheckInOperation {

    private final ReservationService reservations;
    private final AccessService access;

    public CheckInOperation(ReservationService reservations, AccessService access) {
        this.reservations = reservations;
        this.access = access;
    }

    public Reservation execute(Long reservationId, String owner) {
        Reservation r = reservations.findOwned(reservationId, owner);
        if (r.getStatus() != ReservationStatus.RESERVED) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Only RESERVED reservations can check in (current: " + r.getStatus() + ")");
        }
        if (LocalDate.now().isAfter(r.getCheckOut().minusDays(1))) {
            throw new ApiException(HttpStatus.CONFLICT, "The stay is already over");
        }
        r.setStatus(ReservationStatus.CHECKED_IN);
        r.setDigitalKey(access.generateDigitalKey(r));
        return reservations.save(r);
    }
}
