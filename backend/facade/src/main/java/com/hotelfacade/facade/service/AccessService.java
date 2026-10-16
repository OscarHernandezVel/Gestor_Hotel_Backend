package com.hotelfacade.facade.service;

import com.hotelfacade.facade.model.Reservation;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AccessService {

    private final SecureRandom random = new SecureRandom();

    /** Generates the digital room key delivered at check-in, e.g. "R101-483920". */
    public String generateDigitalKey(Reservation reservation) {
        int code = 100000 + random.nextInt(900000);
        return "R" + reservation.getRoom().getRoomNumber() + "-" + code;
    }
}
