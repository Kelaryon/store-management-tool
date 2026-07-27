package com.kelaryon.store_management_tool.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {
}
