package io.github.ptus04.server.security;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class CustomUserDetails implements UserDetails {
    private UUID id;
    private String name;
    private String password;
    private Set<GrantedAuthority> authorities;
    private Instant disabledAt;

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @NonNull String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isEnabled() {
        return disabledAt == null;
    }
}
