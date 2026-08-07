package com.kelaryon.store_management_tool.auth;

import com.kelaryon.store_management_tool.data.*;
import com.kelaryon.store_management_tool.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequestDTO) {
        return authService.signup(signupRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.login(loginRequestDTO);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenRefreshResponseDTO> refreshAccessToken(@Valid @RequestBody TokenRefreshRequestDTO tokenRefreshRequestDTO) {
        return authService.refreshAccessToken(tokenRefreshRequestDTO);
    }

}