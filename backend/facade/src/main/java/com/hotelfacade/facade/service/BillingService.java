package com.hotelfacade.facade.service;

import com.hotelfacade.facade.facade.Invoice;
import com.hotelfacade.facade.facade.InvoiceLine;
import com.hotelfacade.facade.model.AdditionalCharge;
import com.hotelfacade.facade.model.Reservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillingService {

    private static final BigDecimal VAT = new BigDecimal("0.19");

    public String getInvoiceCode(Reservation reservation) {
        return "INV-" + Year.now() + "-" + String.format("%06d", reservation.getId());
    }

    public Invoice getBreakdown(Reservation r) {
        List<InvoiceLine> lines = new ArrayList<>();
        lines.add(new InvoiceLine(
                r.getRoom().getType() + " room " + r.getRoom().getRoomNumber()
                        + " (" + r.getNights() + " nights, " + r.getSeason() + " season)",
                r.getRoomTotal()));
        for (AdditionalCharge c : r.getCharges()) {
            lines.add(new InvoiceLine(c.getType() + " x " + c.getDays() + " days", c.getTotal()));
        }
        BigDecimal subtotal = lines.stream().map(InvoiceLine::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(VAT).setScale(2, RoundingMode.HALF_UP);
        return new Invoice(getInvoiceCode(r), r.getId(), r.getGuestName(), lines,
                subtotal, tax, subtotal.add(tax));
    }
}
