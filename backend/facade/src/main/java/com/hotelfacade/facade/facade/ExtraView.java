package com.hotelfacade.facade.facade;

import com.hotelfacade.facade.model.AdditionalType;

import java.math.BigDecimal;

public record ExtraView(AdditionalType type, int days, BigDecimal total) {
}
