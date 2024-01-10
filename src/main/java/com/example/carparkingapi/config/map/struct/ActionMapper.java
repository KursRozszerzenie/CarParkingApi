package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.action.edit.action.EditAction;
import com.example.carparkingapi.dto.ActionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ActionMapper {

    @Mapping(target = "entityId", expression = "java(mapEntityId(action))")
    @Mapping(target = "entityType", expression = "java(mapEntityType(action))")
    @Mapping(target = "fieldName", expression = "java(mapFieldName(action))")
    @Mapping(target = "oldValue", expression = "java(mapOldValue(action))")
    @Mapping(target = "newValue", expression = "java(mapNewValue(action))")
    public abstract ActionDTO actionToActionDTO(Action action);

    protected Long mapEntityId(Action action) {
        if (!(action instanceof EditAction)) {
            return null;
        }
        return ((EditAction) action).getEntityId();
    }

    protected String mapEntityType(Action action) {
        if (!(action instanceof EditAction)) {
            return null;
        }
        return ((EditAction) action).getEntityType();
    }

    protected String mapFieldName(Action action) {
        if (!(action instanceof EditAction)) {
            return null;
        }
        return ((EditAction) action).getFieldName();
    }

    protected String mapOldValue(Action action) {
        if (!(action instanceof EditAction)) {
            return null;
        }
        return ((EditAction) action).getOldValue();
    }

    protected String mapNewValue(Action action) {
        if (!(action instanceof EditAction)) {
            return null;
        }
        return ((EditAction) action).getNewValue();
    }
}
