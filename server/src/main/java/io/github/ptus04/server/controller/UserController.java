package io.github.ptus04.server.controller;

import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.CarouselService;
import io.github.ptus04.server.service.ProductService;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/thong-tin-tai-khoan")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String getProfilePage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        model.addAttribute("user", userService.getUserById(details.getId()));
        return "user/profile";
    }
}
