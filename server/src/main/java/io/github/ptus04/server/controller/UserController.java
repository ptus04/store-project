package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.StorageService;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;


@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final StorageService storageService;

    @GetMapping("/thong-tin-tai-khoan")
    public String getProfilePage(Model model, @AuthenticationPrincipal CustomUserDetails details) {
        if (details == null) {
            log.warn("Accessing profile page without authentication");
            return "redirect:/dang-nhap";
        }
        log.info("Displaying profile for user: {}", details.getPhone());
        model.addAttribute("user", details);
        return "user/profile";
    }

    @PostMapping("/thong-tin-tai-khoan/cap-nhat")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails details,
                                String name,
                                String email,
                                @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                HttpServletRequest request) {
        if (details == null) {
            return "redirect:/dang-nhap";
        }

        log.info("Updating profile for user ID: {}. Name: {}, Email: {}", details.getId(), name, email);

        String avatarFilename = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            log.info("Received avatar file: {}, size: {} bytes", avatarFile.getOriginalFilename(), avatarFile.getSize());
            avatarFilename = storageService.store(avatarFile);
            log.info("Stored avatar as: {}", avatarFilename);
        }

        UserResponse updatedUser = userService.updateProfile(details.getId(), name, email, avatarFilename);
        log.info("Profile updated in database for user ID: {}", updatedUser.id());

        // Update Security Context to reflect changes in current session
        CustomUserDetails newDetails = details.toBuilder()
                .name(updatedUser.name())
                .email(updatedUser.email())
                .avatar(updatedUser.avatar())
                .build();
        
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newDetails, details.getPassword(), details.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        // IMPORTANT: Manually save to Session for Redis persistence
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        
        log.info("Security context updated and persisted for user ID: {}", updatedUser.id());

        return "redirect:/thong-tin-tai-khoan";
    }

}
