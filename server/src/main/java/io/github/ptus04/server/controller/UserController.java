package io.github.ptus04.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UserController {
    @GetMapping("dang-nhap")
    public String getLoginPage() {
        return "user/login";
    }
}
