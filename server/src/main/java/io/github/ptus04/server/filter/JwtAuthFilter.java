package io.github.ptus04.server.filter;

import io.github.ptus04.server.dto.internal.CustomUserDetails;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtils.isValid(token)) {
                Claims claims = jwtUtils.parseToken(token);
                UUID userId = UUID.fromString(claims.getSubject());
                String name = claims.get("name", String.class);
                String role = claims.get("role", String.class);

                // load user from DB to check disabledAt
                Optional<User> dbUser = userRepository.findById(userId);
                if (dbUser.isPresent()) {
                    User u = dbUser.get();
                    if (u.getDisabledAt() != null) {
                        // If account is disabled, immediately reject the request
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        String msg = "{\"message\":\"Tài khoản của bạn đã bị vô hiệu hóa\"}";
                        response.getWriter().write(msg);
                        return;
                    }
                }

                CustomUserDetails userDetails = CustomUserDetails.builder()
                        .id(userId)
                        .name(name)
                        .password("")
                        .authorities(Set.of(new SimpleGrantedAuthority("ROLE_" + role)))
                        .build();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}