package io.github.ptus04.server.security;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Builder(toBuilder = true)
public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;
    private UUID id;

    private String name;
    private String phone;
    private String email;
    private String avatar;

    private String password;
    private Set<GrantedAuthority> authorities;

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @NonNull String getUsername() {
        return id.toString();
    }
}
