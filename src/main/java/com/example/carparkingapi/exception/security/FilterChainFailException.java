package com.example.carparkingapi.exception.security;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FilterChainFailException extends RuntimeException {
    public FilterChainFailException(String message) {
        super(message);
    }
}
