package com.lendhand.app.lendhandservice.controller;


import com.lendhand.app.lendhandservice.dto.UserRegistrationDto;
import com.lendhand.app.lendhandservice.entity.User;
import com.lendhand.app.lendhandservice.exception.UserAlreadyExistsException;
import com.lendhand.app.lendhandservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }


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

        try {
            User user = userService.registerUser(userDto);

            redirectAttributes.addFlashAttribute("userEmail", user.getEmail());
            redirectAttributes.addFlashAttribute("username", user.getUsername());
            return "redirect:/auth/register/success";
        } catch (UserAlreadyExistsException e) {
            bindingResult.reject("registration.error", e.getMessage());

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", bindingResult);
            redirectAttributes.addFlashAttribute("userDto", userDto);
            return "redirect:/auth/register";
        } catch (Exception e) {
            bindingResult.reject("registration.error", "Произошла ошибка при регистрации. Попробуйте позже.");

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", bindingResult);
            redirectAttributes.addFlashAttribute("userDto", userDto);
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/register/success")
    public String registerSuccessPage(@ModelAttribute("userEmail") String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return "redirect:/auth/register";
        }
        return "registration-success";
    }

}
