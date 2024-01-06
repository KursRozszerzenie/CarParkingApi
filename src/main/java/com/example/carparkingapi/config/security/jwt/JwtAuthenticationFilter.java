package com.example.carparkingapi.config.security.jwt;

import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

//    W niektorych miejscach moze dodac logowanie za pomoca Logger @Log4j2

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }


        final String authenticationHeader = request.getHeader("Authorization");

        if (Objects.isNull(authenticationHeader) || !authenticationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: No JWT token provided");
            return;
        }

        String jsonWebToken = authenticationHeader.substring(7);
        try {
            String userLogin = jwtService.extractUserLogin(jsonWebToken);
            if (Objects.nonNull(userLogin) && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.isTokenValid(jsonWebToken, getUserDetails(jsonWebToken))) {
                    setAuthenticationContext(jsonWebToken, request);
                }
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("expired", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: JWT token expired");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid JWT token");
//            Logger.error(e.getMessage, e);
            return;
        }

        filterChain.doFilter(request, response);
    }


    private void setAuthenticationContext(String jsonWebToken, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(jsonWebToken);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private UserDetails getUserDetails(String jsonWebToken) {
        Claims claims = jwtService.extractAllClaims(jsonWebToken);
        String username = jwtService.extractUserLogin(jsonWebToken);
        String roleString = claims.get("role", String.class);
        Role role = null;
        if (roleString != null) {
            roleString = roleString.replace("Role.", "");
            role = Role.valueOf(roleString);
        } else {
            throw new NullPointerException("Role is null");
        }

        Customer userDetails = new Customer();
        userDetails.setUsername(username);
        userDetails.setRole(role);

        return userDetails;
    }
}
