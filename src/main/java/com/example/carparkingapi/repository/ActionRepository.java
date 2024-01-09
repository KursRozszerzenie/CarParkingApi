package com.example.carparkingapi.repository;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.domain.Admin;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    @NotNull Page<Action> findAll(@NotNull Pageable pageable);

    List<Action> findByCreatedByIdOrLastModifiedById(Long createdById, Long lastModifiedById);
}
