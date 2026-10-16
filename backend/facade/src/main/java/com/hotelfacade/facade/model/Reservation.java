package com.hotelfacade.facade.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String guestName;

    @Column(nullable = false, length = 120)
    private String guestEmail;

    /** Username of the logged-in user who made the reservation. */
    @Column(nullable = false, length = 50)
    private String owner;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_id")
    private HotelRoom room;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Season season;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.RESERVED;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal roomTotal;

    @Column(length = 20)
    private String digitalKey;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdditionalCharge> charges = new ArrayList<>();

    protected Reservation() {
    }

    public Reservation(String guestName, String guestEmail, String owner, HotelRoom room,
                       LocalDate checkIn, LocalDate checkOut, Season season, BigDecimal roomTotal) {
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.owner = owner;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.season = season;
        this.roomTotal = roomTotal;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public Long getId() { return id; }
    public String getGuestName() { return guestName; }
    public String getGuestEmail() { return guestEmail; }
    public String getOwner() { return owner; }
    public HotelRoom getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public Season getSeason() { return season; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public BigDecimal getRoomTotal() { return roomTotal; }
    public String getDigitalKey() { return digitalKey; }
    public void setDigitalKey(String digitalKey) { this.digitalKey = digitalKey; }
    public List<AdditionalCharge> getCharges() { return charges; }
}
