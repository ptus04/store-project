package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.OrderService;
import io.github.ptus04.server.service.UserService;
import io.github.ptus04.server.dto.request.UserAddressUpdateRequest;
import io.github.ptus04.server.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserAddressService userAddressService;
    private final OrderService orderService;

    @GetMapping
    public String getProfilePage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        model.addAttribute("user", userService.getUserById(details.getId()));
        model.addAttribute("addresses", userAddressService.getAddresses(details.getId()));
        model.addAttribute("orders", orderService.searchOrdersByUserId(details.getId(), null, 0, 10));
        if (!model.containsAttribute("addressRequest")) {
            model.addAttribute("addressRequest", UserAddressUpdateRequest.builder().build());
        }
        return "user/profile";
    }

    @GetMapping("/update")
    public String getUpdateProfilePage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        if (!model.containsAttribute("updateProfileRequest")) {
            UserResponse userResponse = userService.getUserById(details.getId());
            model.addAttribute("updateProfileRequest", userMapper.toUserProfileUpdateRequest(userMapper.toEntity(userResponse)));
        }
        return "user/profile-edit";
    }

    @org.springframework.web.bind.annotation.PostMapping("/update")
    public String updateProfile(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("updateProfileRequest") io.github.ptus04.server.dto.request.UserProfileUpdateRequest request,
            org.springframework.validation.BindingResult bindingResult,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails details) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            return "redirect:/profile/update";
        }

        try {
            UserResponse currentUser = userService.getUserById(details.getId());
            UserResponse updatedUser = userService.updateProfile(details.getId(), request);
            if (isNewEmailAddedOrChanged(currentUser.email(), updatedUser.email())) {
                redirectAttributes.addFlashAttribute("success",
                        "Thông tin đã được cập nhật. Mã OTP xác thực đã được gửi về Gmail của bạn.");
            }
            return "redirect:/profile";
        } catch (io.github.ptus04.server.exception.PhoneExistedException e) {
            bindingResult.rejectValue("phone", "phone.exists", e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            return "redirect:/profile/update";
        } catch (io.github.ptus04.server.exception.BusinessConstraintViolationException e) {
            bindingResult.rejectValue("email", "email.exists", e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            return "redirect:/profile/update";
        }
    }

    @PostMapping("/email/verify")
    public String verifyEmail(
            @RequestParam("otp") String otp,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        try {
            userService.verifyProfileEmail(details.getId(), otp);
            redirectAttributes.addFlashAttribute("success", "Email đã được xác thực thành công.");
        } catch (io.github.ptus04.server.exception.BusinessConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("emailVerifyError", e.getMessage());
        }

        return "redirect:/profile";
    }

    private boolean isNewEmailAddedOrChanged(String currentEmail, String updatedEmail) {
        String current = StringUtils.hasText(currentEmail) ? currentEmail.trim().toLowerCase() : null;
        String updated = StringUtils.hasText(updatedEmail) ? updatedEmail.trim().toLowerCase() : null;
        return updated != null && !updated.equals(current);
    }
}
