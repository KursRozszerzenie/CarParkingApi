package com.example.carparkingapi.dto;

import com.example.carparkingapi.model.ActionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ActionDTO {

    private Long id;

    private ActionType actionType;

    private LocalDate createdDate;

    private LocalDate lastModifiedDate;

    private Long entityId;

    private String entityType;

    private String fieldName;

    private String oldValue;

    private String newValue;

}
