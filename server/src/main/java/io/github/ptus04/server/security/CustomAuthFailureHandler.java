package io.github.ptus04.server.security;

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

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException exception
    ) throws IOException {
        String error;
        switch (exception) {
            case BadCredentialsException ignored -> error = "Sai tài khoản hoặc mật khẩu";
            case LockedException ignored -> error = "Tài khoản bị khóa";
            case DisabledException ignored -> error = "Tài khoản bị vô hiệu hóa";
            default -> error = "Đăng nhập thất bại";
        }

        request.getSession().setAttribute("error", error);
        response.sendRedirect("/auth/login");
    }
}