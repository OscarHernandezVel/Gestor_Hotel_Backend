package com.hotelfacade.facade.service;

import com.hotelfacade.facade.exception.ApiException;
import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservations;

    public ReservationService(ReservationRepository reservations) {
        this.reservations = reservations;
    }

    /** Loads a reservation and checks that it belongs to the given user. */
    public Reservation findOwned(Long id, String owner) {
        Reservation r = reservations.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reservation " + id + " not found"));
        if (!r.getOwner().equals(owner)) {
            // Same message as "not found" so ids of other users are not revealed.
            throw new ApiException(HttpStatus.NOT_FOUND, "Reservation " + id + " not found");
        }
        return r;
    }

    public List<Reservation> findAllOf(String owner) {
        return reservations.findByOwnerOrderByIdDesc(owner);
    }

    public Reservation save(Reservation r) {
        return reservations.save(r);
    }
}
