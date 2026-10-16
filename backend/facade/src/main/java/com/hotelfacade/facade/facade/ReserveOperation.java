package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.builder.ReservationDraft;
import com.hotelfacade.facade.model.HotelRoom;
import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.service.AdditionalChargeService;
import com.hotelfacade.facade.service.FeeService;
import com.hotelfacade.facade.service.ReservationService;
import com.hotelfacade.facade.service.RoomService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

/** Subsystems used: RoomService + FeeService + AdditionalChargeService + ReservationService. */
@Component
public class ReserveOperation {

    private final RoomService rooms;
    private final FeeService fees;
    private final AdditionalChargeService extras;
    private final ReservationService reservations;

    public ReserveOperation(RoomService rooms, FeeService fees, AdditionalChargeService extras,
                            ReservationService reservations) {
        this.rooms = rooms;
        this.fees = fees;
        this.extras = extras;
        this.reservations = reservations;
    }

    public Reservation execute(ReservationDraft draft) {
        HotelRoom room = rooms.findFreeRoom(draft.roomType(), draft.checkIn(), draft.checkOut());
        long nights = ChronoUnit.DAYS.between(draft.checkIn(), draft.checkOut());
        BigDecimal roomTotal = fees.getPriceOfRoom(draft.roomType(), draft.season(), nights);

        Reservation reservation = new Reservation(draft.guestName(), draft.guestEmail(), draft.owner(),
                room, draft.checkIn(), draft.checkOut(), draft.season(), roomTotal);
        draft.extras().forEach(e -> extras.addService(reservation, e.type(), e.days()));
        return reservations.save(reservation);
    }
}
