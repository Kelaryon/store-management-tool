package com.kelaryon.store_management_tool.data;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "account_tokens")
public class AccountToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long accountId;
    @NotBlank
    private String tokenHash;
    private String tokenType;
    @NotNull
    private Instant createdDate;
    @NotNull
    private Instant expirationDate;
    private Instant revokedDate;
}
