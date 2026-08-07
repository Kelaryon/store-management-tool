package com.kelaryon.store_management_tool.services;


import com.kelaryon.store_management_tool.data.*;
import com.kelaryon.store_management_tool.repository.AccountRepository;
import com.kelaryon.store_management_tool.repository.AccountTokenRepository;
import com.kelaryon.store_management_tool.security.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final AuthUtils authUtils;
    private final AccountRepository accountRepository;
    private final AccountTokenRepository accountTokenRepository;

    public AuthService(AuthenticationManager authManager, AuthUtils authUtils, AccountRepository accountRepository, AccountTokenRepository accountTokenRepository) {
        this.authManager = authManager;
        this.authUtils = authUtils;
        this.accountRepository = accountRepository;
        this.accountTokenRepository = accountTokenRepository;
    }


    @Transactional
    public ResponseEntity<SignupResponseDTO> signup(SignupRequestDTO signupRequestDTO) {
        if (accountRepository.existsByEmail(signupRequestDTO.email())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new SignupResponseDTO("Email already used"));
        }
        Account account = Account
                .builder()
                .email(signupRequestDTO.email())
                .passwordHash(authUtils.generateHashedPassword(signupRequestDTO.password()))
                .creationDate(new Date())
                .activated(false)
                .build();
        accountRepository.save(account);
        return ResponseEntity.ok(
                new SignupResponseDTO("Account created successfully"));
    }

    @Transactional
    public ResponseEntity<LoginResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.email(),
                        loginRequestDTO.password())
        );
        if (!(authentication.getPrincipal() instanceof AccountDetailsDTO accountDetailsDTO)) {
            throw new IllegalStateException("Authentication principal is not an AccountDetailsDTO");
        }
        String accountJWT = authUtils.generateAccountAccessJWT(accountDetailsDTO.getId());
        String accountRefreshJWT = authUtils.generateAccountRefreshJWT(accountDetailsDTO.getId());
        accountTokenRepository.deactivateUserRefreshTokens(accountDetailsDTO.getId());
        accountTokenRepository.save(createAccountTokenForRefreshToken(accountDetailsDTO.getId(), accountRefreshJWT));
        return ResponseEntity.ok().body(new LoginResponseDTO(accountJWT, accountRefreshJWT));
    }

    @Transactional
    public ResponseEntity<TokenRefreshResponseDTO> refreshAccessToken(TokenRefreshRequestDTO tokenRefreshRequestDTO) {
        String refreshToken = tokenRefreshRequestDTO.refreshToken();
        Long accountIdFromToken = authUtils.getAccountIdFromRefreshToken(refreshToken);
        if (accountIdFromToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT Refresh token");
        }
        AccountToken refreshTokenHash = accountTokenRepository
                .findActiveTokenByAccountIdAndTokenType(accountIdFromToken, "refreshToken");
        if (refreshTokenHash == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT Refresh token");
        }
        if (!authUtils.encodingMatches(refreshToken, refreshTokenHash.getTokenHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT Refresh token");
        }
        String accessToken = authUtils.generateAccountAccessJWT(accountIdFromToken);
        accountTokenRepository.deactivateUserRefreshTokens(accountIdFromToken);
        String accountRefreshJWT = authUtils.generateAccountRefreshJWT(accountIdFromToken);
        accountTokenRepository.save(createAccountTokenForRefreshToken(accountIdFromToken, accountRefreshJWT));
        return ResponseEntity.ok(new TokenRefreshResponseDTO(accessToken, accountRefreshJWT));
    }

    private AccountToken createAccountTokenForRefreshToken(Long accountId, String accountRefreshJWT) {
        String refreshJWTHash = authUtils.generateHashedString(accountRefreshJWT);
        return AccountToken
                .builder()
                .accountId(accountId)
                .tokenHash(refreshJWTHash)
                .createdDate(Instant.now())
                .expirationDate(Instant.now().plus(15, ChronoUnit.DAYS))
                .tokenType("refreshToken")
                .build();
    }

}
