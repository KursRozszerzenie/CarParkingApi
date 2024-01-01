package com.example.carparkingapi.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminCommand {

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "password cannot be blank")
    private String password;
}
