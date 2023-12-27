package com.example.carparkingapi.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    @Value(value = "${jwtSigningKey}")
    private String jwtSigningKey;

    protected String extractUserLogin(String jsonWebToken) {
        return extractClaim(jsonWebToken, Claims::getSubject);
    }

    protected <T> T extractClaim(String jsonWebToken, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jsonWebToken);
        return claimsResolver.apply(claims);
    }

    protected Claims extractAllClaims(String jsonWebToken){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jsonWebToken)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    protected boolean isTokenValid(String jsonWebToken, UserDetails userDetails){
        String[] jwtSubject = extractUserLogin(jsonWebToken).split(",");
        final String role = (String) extractClaim(jsonWebToken, claims -> claims.get("role"));
        return jwtSubject[1].equals(userDetails.getUsername()) &&
                !isTokenExpired(jsonWebToken) &&
                Objects.equals(role, userDetails.getAuthorities().iterator().next().getAuthority());
    }

    private boolean isTokenExpired(String jsonWebToken) {
        return extractExpiration(jsonWebToken).before(new Date());
    }

    private Date extractExpiration(String jsonWebToken) {
        return extractClaim(jsonWebToken, Claims::getExpiration);
    }


}
