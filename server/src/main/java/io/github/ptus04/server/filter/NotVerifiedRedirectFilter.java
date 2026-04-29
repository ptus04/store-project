package io.github.ptus04.server.filter;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class NotVerifiedRedirectFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final String[] excludePatterns = {"/xac-minh", "/css", "/img", "/favicon.png"};

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (Arrays.stream(excludePatterns).anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            if (customUserDetails != null) {
                UserResponse user = userService.getUserById(customUserDetails.getId());
                if (!user.isPhoneVerified()) {
                    response.sendRedirect("/xac-minh");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
