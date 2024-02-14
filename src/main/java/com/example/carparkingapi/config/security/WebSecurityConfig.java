package com.example.carparkingapi.config.security;

import com.example.carparkingapi.config.CustomAccessDeniedHandler;
import com.example.carparkingapi.config.security.jwt.JwtAuthenticationFilter;
import com.example.carparkingapi.exception.security.FilterChainFailException;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.carparkingapi.util.Constants.*;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomerRepository customerRepository;

    private final AdminRepository adminRepository;

    private final CustomAuthenticationEntryPoint entryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(customerRepository, adminRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    try {
                        auth
                                .antMatchers(AUTH_WHITELIST).permitAll()
                                .antMatchers(HttpMethod.GET, CUSTOMER_URL).hasAuthority(USER)
                                .antMatchers(HttpMethod.POST, CUSTOMER_URL).hasAuthority(USER)
                                .antMatchers(HttpMethod.PUT, CUSTOMER_URL).hasAuthority(USER)
                                .antMatchers(HttpMethod.DELETE, CUSTOMER_URL).hasAuthority(USER)
                                .antMatchers(HttpMethod.GET, ADMIN_URL).hasAuthority(ADMIN)
                                .antMatchers(HttpMethod.POST, ADMIN_URL).hasAuthority(ADMIN)
                                .antMatchers(HttpMethod.PUT, ADMIN_URL).hasAuthority(ADMIN)
                                .antMatchers(HttpMethod.DELETE, ADMIN_URL).hasAuthority(ADMIN)
                                .antMatchers(HttpMethod.POST, AUTH_URL).permitAll()
                                .anyRequest()
                                .authenticated()
                                .and()
                                .sessionManagement()
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .and()
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling()
                                .authenticationEntryPoint(entryPoint)
                                .accessDeniedHandler(accessDeniedHandler());
                    } catch (Exception e) {
                        throw new FilterChainFailException(e.getMessage());
                    }
                });

        return http.build();
    }
}