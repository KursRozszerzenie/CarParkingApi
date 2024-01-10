package com.example.carparkingapi.config.jpa;

import com.example.carparkingapi.action.SpringSecurityAuditorAware;
import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@RequiredArgsConstructor
public class JpaConfig {

    private final AdminRepository adminRepository;

    @Bean
    public AuditorAware<Admin> auditorAware() {
        return new SpringSecurityAuditorAware(adminRepository);
    }
}
