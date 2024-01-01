package com.example.carparkingapi.repository;

import com.example.carparkingapi.action.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
