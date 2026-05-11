package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.request.ChangePasswordRequest;
import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.exception.ExistedPhoneNumberException;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("login")
    public String getLoginPage() {
        return "auth/login";
    }

    @GetMapping("register")
    public String getRegisterPage(@ModelAttribute("request") RegistrationRequest request) {
        return "auth/register";
    }

    @PostMapping("register")
    public String registerUser(@Valid @ModelAttribute("request") RegistrationRequest request,
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
        } catch (ExistedPhoneNumberException e) {
            bindingResult.rejectValue("phone", "phone.exists", e.getMessage());
            httpServletResponse.setStatus(HttpStatus.CONFLICT.value());
            return "auth/register";
        }
    }

    @GetMapping(value = "verify-phone")
    public String getOtpVerificationPage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        if (details == null) return "redirect:/";

        long remainingTime = authService.sendPhoneOtp(details.getId());

        model.addAttribute("phone", details.getPhone());
        model.addAttribute("remainingTime", remainingTime);
        return "auth/verify-phone";
    }

    @PostMapping(value = "verify-phone")
    public String verifyOtp(String otp,
                            @AuthenticationPrincipal CustomUserDetails details,
                            RedirectAttributes redirectAttributes) {
        boolean result = authService.verifyOtp(details.getId(), otp);
        if (!result) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP không đúng");
            return "redirect:auth/verify-phone";
        }

        return "redirect:/";
    }
}
