package com.kelaryon.store_management_tool.security;

import com.kelaryon.store_management_tool.data.AccountDetailsDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Log4j2
@Component
public final class AuthUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private SecretKey key;
    private final PasswordEncoder encoder;

    public AuthUtils(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @PostConstruct
    void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateHashedPassword(String password) {
        return encoder.encode(password);
    }

    public String generateAccountJWT(AccountDetailsDTO accountDetailsDTO) {
        return Jwts.builder()
                .subject(accountDetailsDTO.getEmail())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();
    }

    public String getEmailFromJWT(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return null;
        }
        return claims.getSubject();
    }
}
