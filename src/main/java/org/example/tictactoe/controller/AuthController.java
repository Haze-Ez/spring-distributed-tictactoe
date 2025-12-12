package org.example.tictactoe.controller;

import org.example.tictactoe.AppUser;
import org.example.tictactoe.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // show login page
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    // show register page
    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    // handle register
    @PostMapping("/register")
    public String register(@ModelAttribute("user") AppUser user, Model model) {
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        repo.save(user);

        return "redirect:/login?registered";
    }
}
