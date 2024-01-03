package com.example.carparkingapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidFieldNameException extends RuntimeException{
    public InvalidFieldNameException(String message) {
        super(message);
    }
}
