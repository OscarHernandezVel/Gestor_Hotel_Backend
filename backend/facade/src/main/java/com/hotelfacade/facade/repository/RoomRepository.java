package com.hotelfacade.facade.repository;

import com.hotelfacade.facade.model.HotelRoom;
import com.hotelfacade.facade.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<HotelRoom, Long> {
    List<HotelRoom> findByType(RoomType type);
}
