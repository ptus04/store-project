package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.request.UserAddressUpdateRequest;
import io.github.ptus04.server.dto.internal.CustomUserDetails;
import io.github.ptus04.server.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/profile/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    public String getAddresses() {
        return "redirect:/profile";
    }

    @PostMapping
    public String addAddress(
            @Valid @ModelAttribute("addressRequest") UserAddressUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails details) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addressRequest", bindingResult);
            redirectAttributes.addFlashAttribute("addressRequest", request);
            redirectAttributes.addFlashAttribute("showAddressModal", true);
            return "redirect:/profile";
        }

        try {
            userAddressService.addAddress(details.getId(), request);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("addressError", e.getMessage());
            redirectAttributes.addFlashAttribute("addressRequest", request);
            redirectAttributes.addFlashAttribute("showAddressModal", true);
        }

        return "redirect:/profile";
    }

    @PostMapping("/{id}/update")
    public String updateAddress(
            @PathVariable UUID id,
            @Valid @ModelAttribute("addressRequest") UserAddressUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addressRequest", bindingResult);
            redirectAttributes.addFlashAttribute("addressRequest", request);
            redirectAttributes.addFlashAttribute("showEditAddressModal", true);
            redirectAttributes.addFlashAttribute("editAddressId", id);
            return "redirect:/profile";
        }

        try {
            userAddressService.updateAddress(id, request);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("addressError", e.getMessage());
            redirectAttributes.addFlashAttribute("addressRequest", request);
            redirectAttributes.addFlashAttribute("showEditAddressModal", true);
            redirectAttributes.addFlashAttribute("editAddressId", id);
        }

        return "redirect:/profile";
    }

    @PostMapping("/{id}/delete")
    public String deleteAddress(
            @PathVariable UUID id) {
        userAddressService.deleteAddress(id);
        return "redirect:/profile";
    }

    @PostMapping("/{id}/set-default")
    public String setDefaultAddress(
            @PathVariable UUID id) {
        userAddressService.setDefaultAddress(id);
        return "redirect:/profile";
    }
}
