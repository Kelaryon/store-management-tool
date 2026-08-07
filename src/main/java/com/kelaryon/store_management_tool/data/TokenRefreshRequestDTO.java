package com.kelaryon.store_management_tool.data;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequestDTO(
        @NotBlank
        String refreshToken
) {
}
