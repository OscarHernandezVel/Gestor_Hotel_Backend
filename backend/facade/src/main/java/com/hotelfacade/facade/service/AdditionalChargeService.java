package com.hotelfacade.facade.service;

import com.hotelfacade.facade.exception.ApiException;
import com.hotelfacade.facade.model.AdditionalCharge;
import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.model.ReservationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdditionalChargeService {

    private final FeeService fees;

    public AdditionalChargeService(FeeService fees) {
        this.fees = fees;
    }

    /** Adds an extra service (spa, breakfast, transfer) to an open reservation. */
    public AdditionalCharge addService(Reservation reservation, AdditionalType type, int days) {
        if (reservation.getStatus() != ReservationStatus.RESERVED
                && reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Services can only be added to RESERVED or CHECKED_IN reservations");
        }
        if (days < 1 || days > reservation.getNights()) {
            throw new IllegalArgumentException(
                    "Days must be between 1 and the number of nights (" + reservation.getNights() + ")");
        }
        AdditionalCharge charge = new AdditionalCharge(reservation, type, days,
                fees.getPriceOfAdditional(type, days));
        reservation.getCharges().add(charge);
        return charge;
    }

    public BigDecimal totalServices(Reservation reservation) {
        return reservation.getCharges().stream()
                .map(AdditionalCharge::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
