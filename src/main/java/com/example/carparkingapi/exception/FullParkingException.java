package com.example.carparkingapi.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FullParkingException extends RuntimeException {
    public FullParkingException(String message) {
        super(message);
    }
}
