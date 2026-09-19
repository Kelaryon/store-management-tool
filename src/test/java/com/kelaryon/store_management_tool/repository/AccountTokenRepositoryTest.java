package com.kelaryon.store_management_tool.repository;

import com.kelaryon.store_management_tool.data.Account;
import com.kelaryon.store_management_tool.data.AccountToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@ActiveProfiles("test")
class AccountTokenRepositoryTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountTokenRepository accountTokenRepository;

    @BeforeEach
    void cleanDb() {
        accountTokenRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void saveAccountRefreshTokenTest() {
        Account account = Account.builder()
                .email("test_mail@mail.com")
                .passwordHash("test_password")
                .creationDate(new Date())
                .activated(false)
                .build();
        Account savedAccount = accountRepository.save(account);
        assertThat(savedAccount.getId()).isNotNull();
        AccountToken savedToken = accountTokenRepository.save(AccountToken
                .builder()
                .tokenHash("hashTest")
                .accountId(savedAccount.getId())
                .tokenType("refresh")
                .createdDate(Instant.now())
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS))
                .build());
        assertThat(savedToken.getId()).isNotNull();
        assertThat(savedToken.getTokenHash()).isNotNull();

    }

}