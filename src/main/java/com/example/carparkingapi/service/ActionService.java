package com.example.carparkingapi.service;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.action.edit.action.EditAction;
import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.repository.ActionRepository;
import com.example.carparkingapi.repository.AdminRepository;
import org.springframework.data.domain.AuditorAware;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    public void saveAction(ActionType actionType) {
        Admin currentAdmin = (Admin) customUserDetailsService.loadUserByUsername(customUserDetailsService.getCurrentUsername());

        Action action = new Action();
        action.setActionType(actionType);
        action.setCreatedBy(currentAdmin);
        action.setLastModifiedBy(currentAdmin);

        actionRepository.save(action);
    }

    @Transactional
    public void saveAction(ActionType actionType, Long entityId, String entityType,
                           String fieldName, String oldValue, String newValue) {

        Admin currentAdmin = (Admin) customUserDetailsService.loadUserByUsername(customUserDetailsService.getCurrentUsername());

        EditAction editAction = new EditAction();
        editAction.setActionType(actionType);
        editAction.setEntityId(entityId);
        editAction.setEntityType(entityType);
        editAction.setFieldName(fieldName);
        editAction.setOldValue(oldValue);
        editAction.setNewValue(newValue);

        editAction.setCreatedBy(currentAdmin);
        editAction.setLastModifiedBy(currentAdmin);

        actionRepository.save(editAction);
    }
}
