package com.kelaryon.store_management_tool.security;

import com.kelaryon.store_management_tool.services.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtils authUtils;

    public static final String BEARER_ = "Bearer ";
    private final CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(CustomUserDetailService customUserDetailService, AuthUtils authUtils) {
        this.customUserDetailService = customUserDetailService;
        this.authUtils = authUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = header.substring(BEARER_.length());
            String email = authUtils.getEmailFromJWT(token);

            if (email == null) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = customUserDetailService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);
        } catch (Exception e) {
            log.error("Failed to set user authentication: {}", e.getMessage());

        }
        filterChain.doFilter(request, response);
    }
}
