package com.example.carparkingapi.exception.parking;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@NoArgsConstructor
public class CarParkingStatusException extends RuntimeException {
    public CarParkingStatusException(String message) {
        super(message);
    }
}
