package com.example.carparkingapi.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class CustomerCommand {

    @NotBlank(message = "first name cannot be blank")
    private String firstName;

    @NotBlank(message = "last name cannot be blank")
    private String lastName;

    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotBlank(message = "password cannot be blank")
    private String password;
}
