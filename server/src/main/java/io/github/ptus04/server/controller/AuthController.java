package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.request.RegistrationRequest;
import io.github.ptus04.server.exception.ExistedPhoneNumberException;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("dang-nhap")
    public String getLoginPage() {
        return "user/login";
    }

    @GetMapping("dang-ky")
    public String getRegisterPage(@ModelAttribute("request") RegistrationRequest registrationRequest) {
        return "user/register";
    }

    @PostMapping("dang-ky")
    public Object registerUser(@Valid @ModelAttribute("request") RegistrationRequest registrationRequest,
                               BindingResult bindingResult,
                               HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("user/register");
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }

        try {
            authService.register(httpSession, registrationRequest);
            return "redirect:/";
        } catch (ExistedPhoneNumberException e) {
            bindingResult.rejectValue("phone", "error.registrationRequest", e.getMessage());
            ModelAndView modelAndView = new ModelAndView("user/register");
            modelAndView.setStatus(HttpStatus.CONFLICT);
            return modelAndView;
        }
    }

    @GetMapping(value = "xac-minh")
    public String getOtpVerificationPage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        if (details == null) {
            return "redirect:/";
        }

        long remainingTime = authService.sendPhoneOtp(details.getId());

        model.addAttribute("phone", details.getPhone());
        model.addAttribute("userId", details.getId());
        model.addAttribute("remainingTime", remainingTime);
        return "user/sms-otp";
    }

    @PostMapping(value = "xac-minh")
    public String verifyOtp(String otp,
                            @AuthenticationPrincipal CustomUserDetails details,
                            RedirectAttributes redirectAttributes) {
        boolean result = authService.verifyOtp(details.getId(), otp);
        if (!result) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP không đúng");
            return "redirect:xac-minh";
        }

        return "redirect:/";
    }
}
