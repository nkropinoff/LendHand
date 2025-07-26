package com.lendhand.app.lendhandservice.controller;


import com.lendhand.app.lendhandservice.dto.UserRegistrationDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        model.addAttribute("registrationSuccess", false);
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationSuccess", false);
            return "registration";
        }

        // ...

        model.addAttribute("registrationSuccess", true);
        model.addAttribute("userEmail", userDto.getEmail());
        return "registration";
    }

}
