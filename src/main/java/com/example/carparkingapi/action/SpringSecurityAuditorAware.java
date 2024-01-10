package com.example.carparkingapi.action;

import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<Admin> {

    private final AdminRepository adminRepository;

    @Override
    public @NotNull Optional<Admin> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .flatMap(authentication -> adminRepository.findAdminByUsername(authentication.getName()));
    }
}
