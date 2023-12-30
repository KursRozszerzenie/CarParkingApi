package com.example.carparkingapi.config.security.authentication;

import com.example.carparkingapi.model.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (Objects.nonNull(request.getAttribute("expired"))) {
            ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, request.getAttribute("expired"));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiError));
        }
    }
}
