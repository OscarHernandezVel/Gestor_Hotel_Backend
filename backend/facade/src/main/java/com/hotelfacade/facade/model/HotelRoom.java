package com.hotelfacade.facade.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hotel_room")
public class HotelRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomType type;

    protected HotelRoom() {
    }

    public HotelRoom(String roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
    }

    public Long getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
}
