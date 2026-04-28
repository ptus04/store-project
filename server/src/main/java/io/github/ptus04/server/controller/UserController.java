package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.UserRegisterRequest;
import io.github.ptus04.server.dto.UserResponse;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("dang-nhap")
    public String getLoginPage(Principal principal) {
        if (principal != null) {
            return "redirect:/tai-khoan";
        }
        return "user/login";
    }

    @GetMapping({"tai-khoan", "thong-tin-tai-khoan"})
    public String getProfilePage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        UserResponse user = userService.getUserProfile(principal.getName());
        
        // Nếu là Admin/Employee, chuyển hướng sang Dashboard (React)
        if (user.role() == UserRoleEnum.ADMIN || user.role() == UserRoleEnum.EMPLOYEE) {
            return "redirect:http://localhost:5173/admin";
        }
        
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("tai-khoan/chinh-sua")
    public String getEditProfilePage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        UserResponse user = userService.getUserProfile(principal.getName());
        model.addAttribute("user", user);
        return "user/edit-profile";
    }

    @GetMapping("admin")
    public String getAdminDashboard(Principal principal) {
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        UserResponse user = userService.getUserProfile(principal.getName());
        if (user.role() == UserRoleEnum.ADMIN || user.role() == UserRoleEnum.EMPLOYEE) {
            return "redirect:http://localhost:5173/admin";
        }
        return "redirect:/";
    }

    @GetMapping("dang-ky")
    public String getRegisterPage(@ModelAttribute("user") UserRegisterRequest user) {
        return "user/register";
    }

    @PostMapping("dang-ky")
    public String registerUser(@Valid @ModelAttribute("user") UserRegisterRequest user,
                               BindingResult result,
                               HttpServletRequest request) {
        if (result.hasErrors()) {
            return "user/register";
        }

        try {
            // 1. Đăng ký người dùng mới
            userService.registerUser(user);

            // 2. Đăng nhập tự động
            // Xóa thông tin đăng nhập cũ (nếu có) để tránh xung đột
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            
            // Thực hiện đăng nhập với thông tin vừa đăng ký
            request.login(user.phone(), user.password());
            
            return "redirect:/tai-khoan";
        } catch (Exception e) {
            result.rejectValue("phone", "error.user", e.getMessage());
            return "user/register";
        }
    }
}
