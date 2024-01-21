package com.example.carparkingapi.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditCommand {

    String fieldName;

    String newValue;
}
