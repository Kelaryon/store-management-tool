package com.kelaryon.store_management_tool.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Component
public final class AuthUtils {

    @Value("${jwt.access.secret}")
    private String jwtAccessSecret;
    @Value("${jwt.refresh.secret}")
    private String jwtRefreshSecret;
    @Value("${jwt.hash.secret}")
    private String hashSecret;
    private SecretKey accessKey;
    private SecretKey refreshKey;
    private final PasswordEncoder encoder;
    private Mac mac;

    public AuthUtils(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @PostConstruct
    void init() {
        accessKey = Keys.hmacShaKeyFor(jwtAccessSecret.getBytes(StandardCharsets.UTF_8));
        refreshKey = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes(StandardCharsets.UTF_8));
        try {
            mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec hashKey = new SecretKeySpec(hashSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(hashKey);
        }catch (Exception e){
            log.error("Failed to initialize HMAC because {}",e.getMessage());
        }
    }

    public String generateHashedPassword(String password) {
        return encoder.encode(password);
    }

    public String generateHashedString(String string){
        if(mac == null){
            log.error("Failed to generate hashed String because MAC object failed to initialize");
            return null;
        }
        byte[] hmac = mac.doFinal(string.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hmac) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String generateAccountAccessJWT(Long accountId) {
        return Jwts.builder()
                .subject("user_" + accountId)
                .id(UUID.randomUUID().toString())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(accessKey)
                .compact();
    }

    public String generateAccountRefreshJWT(Long accountId) {
        return Jwts.builder()
                .subject("user_" + accountId)
                .id(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(refreshKey)
                .compact();
    }

    public Long getAccountIdFromAccessToken(String token){
        return getAccountIdFromToken(token,accessKey);
    }
    public Long getAccountIdFromRefreshToken(String token){
        return getAccountIdFromToken(token,refreshKey);
    }

    private Long getAccountIdFromToken(String token, SecretKey secretKey) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return null;
        }
        return Long.parseLong(
                claims.getSubject().replace("user_", ""));
    }

    public boolean encodingMatches(String value, String encodedValue){
        return Objects.equals(generateHashedString(value),encodedValue);
    }
}
