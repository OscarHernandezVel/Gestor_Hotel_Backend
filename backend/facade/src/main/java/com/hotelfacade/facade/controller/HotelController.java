package com.hotelfacade.facade.controller;

import com.hotelfacade.facade.auth.AuthInterceptor;
import com.hotelfacade.facade.builder.ReservationDraft;
import com.hotelfacade.facade.facade.HotelFacade;
import com.hotelfacade.facade.facade.Invoice;
import com.hotelfacade.facade.facade.Quote;
import com.hotelfacade.facade.facade.ReservationView;
import com.hotelfacade.facade.facade.RoomView;
import com.hotelfacade.facade.model.AdditionalType;
import com.hotelfacade.facade.model.RoomType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    public record ExtraRequest(@NotNull AdditionalType type, @Min(1) int days) {
    }

    public record ReservationRequest(
            @NotBlank String guestName,
            @NotBlank @Email String guestEmail,
            @NotNull RoomType roomType,
            @NotNull LocalDate checkIn,
            @NotNull LocalDate checkOut,
            List<@Valid ExtraRequest> extras) {
    }

    private final HotelFacade hotel;

    public HotelController(HotelFacade hotel) {
        this.hotel = hotel;
    }

    @GetMapping("/rooms/availability")
    public List<RoomView> availability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) RoomType type) {
        return hotel.checkAvailability(from, to, type);
    }

    @GetMapping("/quote")
    public Quote quote(
            @RequestParam RoomType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return hotel.quote(type, checkIn, checkOut);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationView> reserve(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user,
                                                   @Valid @RequestBody ReservationRequest req) {
        List<ReservationDraft.Extra> extras = req.extras() == null ? List.of()
                : req.extras().stream().map(e -> new ReservationDraft.Extra(e.type(), e.days())).toList();
        ReservationView view = hotel.reserve(user, req.guestName(), req.guestEmail(), req.roomType(),
                req.checkIn(), req.checkOut(), extras);
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @GetMapping("/reservations")
    public List<ReservationView> myReservations(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user) {
        return hotel.myReservations(user);
    }

    @GetMapping("/reservations/{id}")
    public ReservationView get(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user,
                               @PathVariable Long id) {
        return hotel.getReservation(id, user);
    }

    @PostMapping("/reservations/{id}/services")
    public ReservationView addService(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user,
                                      @PathVariable Long id, @Valid @RequestBody ExtraRequest req) {
        return hotel.addService(id, user, req.type(), req.days());
    }

    @PostMapping("/reservations/{id}/check-in")
    public ReservationView checkIn(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user,
                                   @PathVariable Long id) {
        return hotel.checkIn(id, user);
    }

    @PostMapping("/reservations/{id}/check-out")
    public Invoice checkOut(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user,
                            @PathVariable Long id) {
        return hotel.checkOut(id, user);
    }

    @GetMapping("/reservations/{id}/invoice")
    public Invoice invoice(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user,
                           @PathVariable Long id) {
        return hotel.invoice(id, user);
    }

    @DeleteMapping("/reservations/{id}")
    public ReservationView cancel(@RequestAttribute(AuthInterceptor.USERNAME_ATTRIBUTE) String user,
                                  @PathVariable Long id) {
        return hotel.cancel(id, user);
    }
}
