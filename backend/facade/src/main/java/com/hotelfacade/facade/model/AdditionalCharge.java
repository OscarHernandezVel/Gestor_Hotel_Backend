package com.hotelfacade.facade.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "additional_charge")
public class AdditionalCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdditionalType type;

    @Column(nullable = false)
    private int days;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    protected AdditionalCharge() {
    }

    public AdditionalCharge(Reservation reservation, AdditionalType type, int days, BigDecimal total) {
        this.reservation = reservation;
        this.type = type;
        this.days = days;
        this.total = total;
    }

    public Long getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public AdditionalType getType() { return type; }
    public int getDays() { return days; }
    public BigDecimal getTotal() { return total; }
}
