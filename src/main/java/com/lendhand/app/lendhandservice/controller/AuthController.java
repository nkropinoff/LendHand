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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/register")
    public String registerPage(Model model) {

        if (!model.containsAttribute("userDto")) {
            model.addAttribute("userDto", new UserRegistrationDto());
        }

        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", bindingResult);
            redirectAttributes.addFlashAttribute("userDto", userDto);
            return "redirect:/auth/register";
        }

        // ...

        redirectAttributes.addFlashAttribute("userEmail", userDto.getEmail());
        return "redirect:/auth/register/success";
    }

    @GetMapping("/register/success")
    public String registerSuccessPage(@ModelAttribute("userEmail") String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return "redirect:/auth/register";
        }
        return "registration-success";
    }

}
