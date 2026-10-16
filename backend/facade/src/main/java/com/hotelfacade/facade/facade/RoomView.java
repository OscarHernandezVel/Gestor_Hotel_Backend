package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.model.RoomType;

import java.math.BigDecimal;

public record RoomView(Long id, String roomNumber, RoomType type, BigDecimal basePricePerNight) {
}
