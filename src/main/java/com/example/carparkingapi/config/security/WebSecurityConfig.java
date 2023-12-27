package com.example.carparkingapi.config.security;

import com.example.carparkingapi.config.CustomAccessDeniedHandler;
import com.example.carparkingapi.config.security.jwt.CustomAuthenticationEntryPoint;
import com.example.carparkingapi.config.security.jwt.JwtAuthenticationFilter;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomerRepository customerRepository;

    private final CustomAuthenticationEntryPoint entryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(customerRepository);
    }

//    authenticationManagera
//    Musimy stworzyc sobie serwis do autentykacji i endpoint do autentykaji w sensie do tworenia sobie jwt

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
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .csrf()
                    .disable()
                    .authorizeRequests(authz -> {
                                try {
                                    authz
                                            .antMatchers(HttpMethod.GET, "/api/v1/car/").permitAll()
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
                                    throw new RuntimeException(e);
                                }
                            }
                    ).httpBasic(Customizer.withDefaults());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            return http.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

