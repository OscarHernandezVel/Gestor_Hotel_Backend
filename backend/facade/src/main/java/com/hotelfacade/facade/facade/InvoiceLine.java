package com.hotelfacade.facade.facade;

import java.math.BigDecimal;

public record InvoiceLine(String concept, BigDecimal amount) {
}
