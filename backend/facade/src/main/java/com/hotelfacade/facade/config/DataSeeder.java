package com.hotelfacade.facade.config;

import com.hotelfacade.facade.model.HotelRoom;
import com.hotelfacade.facade.model.RoomType;
import com.hotelfacade.facade.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Creates the hotel rooms the first time the application starts. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoomRepository rooms;

    public DataSeeder(RoomRepository rooms) {
        this.rooms = rooms;
    }

    @Override
    public void run(String... args) {
        if (rooms.count() > 0) {
            return;
        }
        for (int i = 1; i <= 5; i++) {
            rooms.save(new HotelRoom("10" + i, RoomType.SIMPLE));
            rooms.save(new HotelRoom("20" + i, RoomType.DOUBLE));
        }
        rooms.save(new HotelRoom("301", RoomType.SUITE));
        rooms.save(new HotelRoom("302", RoomType.SUITE));
    }
}
