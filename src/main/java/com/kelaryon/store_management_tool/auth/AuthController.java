package com.kelaryon.store_management_tool.auth;

import com.kelaryon.store_management_tool.data.*;
import com.kelaryon.store_management_tool.repository.AccountRepository;
import com.kelaryon.store_management_tool.security.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthenticationManager authManager;
    private final AuthUtils authUtils;
    private final AccountRepository accountRepository;

    public AuthController(
            AuthenticationManager authManager,
            AuthUtils authUtils,
            AccountRepository accountRepository) {
        this.authManager = authManager;
        this.authUtils = authUtils;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
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

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.email(),
                        loginRequestDTO.password())
        );
        if (!(authentication.getPrincipal() instanceof AccountDetailsDTO accountDetailsDTO)) {
            throw new IllegalStateException("Authentication principal is not an AccountDetailsDTO");
        }
        String accountJWT = authUtils.generateAccountJWT(accountDetailsDTO);
        return new LoginResponseDTO(accountJWT);
    }

}