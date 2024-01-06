package com.example.carparkingapi.exception.parking.action;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@NoArgsConstructor
public class LPGNotAllowedException extends RuntimeException {
    public LPGNotAllowedException(String message) {
        super(message);
    }
}
