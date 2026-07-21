package com.kelaryon.store_management_tool.services;

import com.kelaryon.store_management_tool.data.Account;
import com.kelaryon.store_management_tool.data.AccountDetailsDTO;
import com.kelaryon.store_management_tool.repository.AccountRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomUserDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @NonNull
    public AccountDetailsDTO loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with email " + email));

        List<GrantedAuthority> roles = account.getRoles()
                .stream()
                .map(e -> (GrantedAuthority) new SimpleGrantedAuthority(e.getName()))
                .toList();

        return new AccountDetailsDTO(
                email,
                account.getPasswordHash(),
                account.isActivated(),
                roles
        );
    }
}
