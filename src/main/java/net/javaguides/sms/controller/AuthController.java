package net.javaguides.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access_denied";
    }
}
