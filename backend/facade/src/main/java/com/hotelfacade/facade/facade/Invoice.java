package com.hotelfacade.facade.facade;

import java.math.BigDecimal;
import java.util.List;

public record Invoice(String code, Long reservationId, String guestName, List<InvoiceLine> lines,
                      BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
}
