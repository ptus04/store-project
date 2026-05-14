package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.request.UserChangePasswordRequest;
import io.github.ptus04.server.dto.request.UserRegistrationRequest;
import io.github.ptus04.server.dto.response.PhoneVerificationResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.exception.PhoneExistedException;
import io.github.ptus04.server.exception.UserNotFoundException;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("login")
    public String getLoginPage(HttpSession httpSession, Model model) {
        Object message = httpSession.getAttribute("error");

        if (message != null) {
            model.addAttribute("error", message);
            httpSession.removeAttribute("error");
        }

        return "auth/login";
    }

    @GetMapping("register")
    public String getRegisterPage(@ModelAttribute("request") UserRegistrationRequest request) {
        return "auth/register";
    }

    @PostMapping("register")
    public String registerUser(@Valid @ModelAttribute("request") UserRegistrationRequest request,
                               BindingResult bindingResult,
                               HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse) {
        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return "auth/register";
        }

        try {
            Authentication auth = authService.register(request);
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(auth);
            new HttpSessionSecurityContextRepository()
                    .saveContext(context, httpServletRequest, httpServletResponse);
            return "redirect:auth/verify-phone";
        } catch (PhoneExistedException e) {
            bindingResult.rejectValue("phone", "phone.exists", e.getMessage());
            httpServletResponse.setStatus(HttpStatus.CONFLICT.value());
            return "auth/register";
        }
    }

    @GetMapping(value = "verify-phone")
    public String getOtpVerificationPage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        if (details == null) return "redirect:/";

        PhoneVerificationResponse response = authService.sendPhoneVerification(details.getId());

        model.addAttribute("phone", response.phone());
        model.addAttribute("remainingTime", response.remainingTime());
        return "auth/verify-phone";
    }

    @PostMapping(value = "verify-phone")
    public String verifyOtp(String otp,
                            @AuthenticationPrincipal CustomUserDetails details,
                            Model model) {
        boolean result = authService.verifyOtp(details.getId(), otp);
        if (!result) {
            model.addAttribute("error", "Mã OTP không đúng");
            return "auth/verify-phone";
        }

        return "redirect:/";
    }

    @GetMapping(value = "change-password")
    public String getChangePasswordPage(@RequestParam(required = false) String phone,
                                        @AuthenticationPrincipal CustomUserDetails details,
                                        Model model) {
        if (details != null) {
            User user = userRepository.findById(details.getId()).orElseThrow(UserNotFoundException::new);
            phone = user.getPhone();
        }
        long remainingTime = Optional.ofNullable(phone).map(authService::sendPhoneOtp).orElse(0L);

        model.addAttribute("changePasswordRequest", new UserChangePasswordRequest(phone, null, null));
        model.addAttribute("remainingTime", remainingTime);
        return "auth/change-password";
    }

    @PostMapping(value = "change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") UserChangePasswordRequest userChangePasswordRequest,
                                 BindingResult bindingResult,
                                 HttpServletResponse httpServletResponse) {
        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return "auth/change-password";
        }

        boolean result = authService.changePassword(userChangePasswordRequest);
        if (!result) {
            bindingResult.rejectValue("otp", "otp.invalid", "Mã OTP không chính xác!");
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return "auth/change-password";
        }

        return "redirect:/";
    }
}
