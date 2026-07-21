package com.kelaryon.store_management_tool.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountDetailsDTO implements UserDetails {

    private String email;
    private String password;
    private boolean activated;
    private List<GrantedAuthority> roles;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    @NonNull
    public String getUsername() {
        return email;
    }

}
