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
                               RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userDto", userDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", bindingResult);
            return "redirect:/auth/register";
        }

        try {
            User user = userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("userEmail", user.getEmail());
            return "redirect:/auth/register/success";

        } catch (UserAlreadyExistsException e) {
            bindingResult.rejectValue("email", "user.exists", e.getMessage());
            redirectAttributes.addFlashAttribute("userDto", userDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", bindingResult);
            return "redirect:/auth/register";

        } catch (Exception e) {
            bindingResult.reject("registration.error", "Произошла ошибка при регистрации. Попробуйте позже.");
            return "registration";
        }
    }

    @GetMapping("/register/success")
    public String registerSuccessPage(Model model) {
        if (!model.containsAttribute("userEmail")) {
            return "redirect:/auth/register";
        }
        return "registration-success";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        try {
            userService.verifyEmailToken(token);
            redirectAttributes.addFlashAttribute("verificationSuccess", "Ваш аккаунт успешно подтвержден! Теперь вы можете войти.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("verificationError", e.getMessage());
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/resend-verification")
    public String resendVerificationPage() {
        return "resend-verification";
    }

    @PostMapping("/resend-verification")
    public String handleResendVerification(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        userService.requestResendVerificationEmail(email);
        redirectAttributes.addFlashAttribute("resendSuccess", "Если аккаунт с таким email существует и не подтвержден, мы отправили новое письмо.");
        return "redirect:/auth/login";
    }

}
