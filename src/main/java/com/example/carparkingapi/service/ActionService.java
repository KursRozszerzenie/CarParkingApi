package com.example.carparkingapi.service;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.repository.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
}