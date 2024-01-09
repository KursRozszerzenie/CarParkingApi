package com.example.carparkingapi.config.jpa;

import com.example.carparkingapi.action.SpringSecurityAuditorAware;
import com.example.carparkingapi.domain.Admin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.carparkingapi.repository.AdminRepository;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    @Autowired
    private AdminRepository adminRepository;

    @Bean
    public AuditorAware<Admin> auditorAware() {
        return new SpringSecurityAuditorAware(adminRepository);
    }
}
