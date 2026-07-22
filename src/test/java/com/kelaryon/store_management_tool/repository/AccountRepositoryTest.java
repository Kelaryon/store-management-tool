package com.kelaryon.store_management_tool.repository;

import com.kelaryon.store_management_tool.data.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void cleanDb() {
        accountRepository.deleteAll();
    }

    @Test
    void saveAccountTest() {
        Account account = Account.builder()
                .email("test_mail@mail.com")
                .passwordHash("test_password")
                .creationDate(new Date())
                .activated(false)
                .build();
        Account savedAccount = accountRepository.save(account);
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getEmail()).isEqualTo(account.getEmail());
    }

}