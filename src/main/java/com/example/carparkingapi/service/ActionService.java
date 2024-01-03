package com.example.carparkingapi.service;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.action.edit.action.EditAction;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.repository.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    @Transactional
    public void logAction(ActionType actionType) {
        Action action = new Action();
        action.setActionType(actionType);
        actionRepository.save(action);
    }

    @Transactional
    public void logAction(ActionType actionType, Long entityId, String entityType,
                          String fieldName, String oldValue, String newValue) {

            EditAction editAction = new EditAction();
            editAction.setActionType(actionType);
            editAction.setEntityId(entityId);
            editAction.setEntityType(entityType);
            editAction.setFieldName(fieldName);
            editAction.setOldValue(oldValue);
            editAction.setNewValue(newValue);
            actionRepository.save(editAction);
    }
}