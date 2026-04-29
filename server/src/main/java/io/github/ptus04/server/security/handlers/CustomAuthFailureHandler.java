package io.github.ptus04.server.security.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException exception
    ) throws IOException {
        String message;
        switch (exception) {
            case BadCredentialsException ignored -> message = "Sai tài khoản hoặc mật khẩu";
            case LockedException ignored -> message = "Tài khoản bị khóa";
            case DisabledException ignored -> message = "Tài khoản bị vô hiệu hóa";
            default -> message = "Đăng nhập thất bại";
        }

        response.sendRedirect("/dang-nhap?error&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}