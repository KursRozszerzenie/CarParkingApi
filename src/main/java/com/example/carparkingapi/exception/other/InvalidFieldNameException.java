package com.example.carparkingapi.exception.other;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidFieldNameException extends RuntimeException {
    public InvalidFieldNameException(String message) {
        super(message);
    }
}
