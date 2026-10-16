package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.builder.ReservationBuilder;
import com.hotelfacade.facade.builder.ReservationDraft;
import com.hotelfacade.facade.exception.ApiException;
import com.hotelfacade.facade.model.AdditionalCharge;
import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.Reservation;
import com.hotelfacade.facade.model.ReservationStatus;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.model.Season;
import com.hotelfacade.facade.service.AdditionalChargeService;
import com.hotelfacade.facade.service.BillingService;
import com.hotelfacade.facade.service.FeeService;
import com.hotelfacade.facade.service.ReservationService;
import com.hotelfacade.facade.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * FACADE pattern: the controllers only talk to this class. It hides the
 * rooms, fees, extras, access and billing subsystems behind a simple API.
 */
@Service
@Transactional
public class HotelFacade {

    private final ReserveOperation reserveOp;
    private final CheckInOperation checkInOp;
    private final CheckOutOperation checkOutOp;
    private final AddServiceOperation addServiceOp;
    private final RoomService rooms;
    private final FeeService fees;
    private final ReservationService reservations;
    private final AdditionalChargeService extras;
    private final BillingService billing;

    public HotelFacade(ReserveOperation reserveOp, CheckInOperation checkInOp, CheckOutOperation checkOutOp,
                       AddServiceOperation addServiceOp, RoomService rooms, FeeService fees,
                       ReservationService reservations, AdditionalChargeService extras,
                       BillingService billing) {
        this.reserveOp = reserveOp;
        this.checkInOp = checkInOp;
        this.checkOutOp = checkOutOp;
        this.addServiceOp = addServiceOp;
        this.rooms = rooms;
        this.fees = fees;
        this.reservations = reservations;
        this.extras = extras;
        this.billing = billing;
    }

    @Transactional(readOnly = true)
    public List<RoomView> checkAvailability(LocalDate from, LocalDate to, RoomType type) {
        return rooms.getAvailability(from, to, type).stream()
                .map(r -> new RoomView(r.getId(), r.getRoomNumber(), r.getType(),
                        r.getType().getBasePricePerNight()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Quote quote(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        if (type == null || checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("type, checkIn and checkOut are required and checkOut must be after checkIn");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        Season season = Season.fromDate(checkIn);
        return new Quote(type, checkIn, checkOut, nights, season, fees.getPriceOfRoom(type, season, nights));
    }

    public ReservationView reserve(String owner, String guestName, String guestEmail, RoomType type,
                                   LocalDate checkIn, LocalDate checkOut, List<ReservationDraft.Extra> requested) {
        ReservationBuilder builder = ReservationBuilder.builder()
                .owner(owner)
                .guest(guestName, guestEmail)
                .roomType(type)
                .stay(checkIn, checkOut);
        if (requested != null) {
            requested.forEach(e -> builder.withExtra(e.type(), e.days()));
        }
        return toView(reserveOp.execute(builder.build()));
    }

    @Transactional(readOnly = true)
    public List<ReservationView> myReservations(String owner) {
        return reservations.findAllOf(owner).stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public ReservationView getReservation(Long id, String owner) {
        return toView(reservations.findOwned(id, owner));
    }

    public ReservationView addService(Long id, String owner, AdditionalType type, int days) {
        return toView(addServiceOp.execute(id, owner, type, days));
    }

    public ReservationView checkIn(Long id, String owner) {
        return toView(checkInOp.execute(id, owner));
    }

    public Invoice checkOut(Long id, String owner) {
        return checkOutOp.execute(id, owner);
    }

    public ReservationView cancel(Long id, String owner) {
        Reservation r = reservations.findOwned(id, owner);
        if (r.getStatus() != ReservationStatus.RESERVED) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Only RESERVED reservations can be cancelled (current: " + r.getStatus() + ")");
        }
        r.setStatus(ReservationStatus.CANCELLED);
        return toView(reservations.save(r));
    }

    @Transactional(readOnly = true)
    public Invoice invoice(Long id, String owner) {
        return billing.getBreakdown(reservations.findOwned(id, owner));
    }

    private ReservationView toView(Reservation r) {
        BigDecimal extrasTotal = extras.totalServices(r);
        List<ExtraView> extraViews = r.getCharges().stream()
                .map((AdditionalCharge c) -> new ExtraView(c.getType(), c.getDays(), c.getTotal()))
                .toList();
        String key = r.getStatus() == ReservationStatus.CHECKED_IN ? r.getDigitalKey() : null;
        return new ReservationView(r.getId(), r.getGuestName(), r.getGuestEmail(),
                r.getRoom().getRoomNumber(), r.getRoom().getType(), r.getCheckIn(), r.getCheckOut(),
                r.getNights(), r.getSeason(), r.getStatus(), r.getRoomTotal(), extrasTotal,
                r.getRoomTotal().add(extrasTotal), key, extraViews);
    }
}
