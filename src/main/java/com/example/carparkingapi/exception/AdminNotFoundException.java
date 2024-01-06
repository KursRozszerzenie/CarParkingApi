package com.example.carparkingapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdminNotFoundException extends RuntimeException{
    public AdminNotFoundException(String message) {
        super(message);
    }

}
