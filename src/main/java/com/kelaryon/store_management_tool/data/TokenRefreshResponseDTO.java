package com.kelaryon.store_management_tool.data;

public record TokenRefreshResponseDTO(
        String accessToken,
        String refreshToken
) {
}
