package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.action.edit.action.EditAction;
import com.example.carparkingapi.dto.ActionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ActionMapper {

    ActionDTO editActionToActionDTO(EditAction editAction);

}
