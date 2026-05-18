package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.UserService;
import io.github.ptus04.server.dto.request.UserAddressUpdateRequest;
import io.github.ptus04.server.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserAddressService userAddressService;

    @GetMapping
    public String getProfilePage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        model.addAttribute("user", userService.getUserById(details.getId()));
        model.addAttribute("addresses", userAddressService.getAddresses(details.getId()));
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
            userService.updateProfile(details.getId(), request);
            return "redirect:/profile";
        } catch (io.github.ptus04.server.exception.PhoneExistedException e) {
            bindingResult.rejectValue("phone", "phone.exists", e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            return "redirect:/profile/update";
        }
    }
}
