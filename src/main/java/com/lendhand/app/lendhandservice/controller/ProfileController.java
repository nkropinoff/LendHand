package com.lendhand.app.lendhandservice.controller;

import com.lendhand.app.lendhandservice.dto.UserProfileUpdateDto;
import com.lendhand.app.lendhandservice.entity.User;
import com.lendhand.app.lendhandservice.entity.UserProfile;
import com.lendhand.app.lendhandservice.service.CustomUserDetails;
import com.lendhand.app.lendhandservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profilePage(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        UserProfile userProfile = user.getUserProfile();

        model.addAttribute("user", user);
        model.addAttribute("userProfile", userProfile);

        if (!model.containsAttribute("profileUpdateDto")) {
            UserProfileUpdateDto userProfileUpdateDto = new UserProfileUpdateDto();
            userProfileUpdateDto.setLocation(userProfile.getLocation());
            userProfileUpdateDto.setAbout(userProfile.getAbout());
            userProfileUpdateDto.setAvatarUrl(userProfile.getAvatarUrl());
            model.addAttribute("profileUpdateDto", userProfileUpdateDto);
        }
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateDto") UserProfileUpdateDto userProfileUpdateDto,
                                BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileUpdateDto", bindingResult);
            redirectAttributes.addFlashAttribute("profileUpdateDto", userProfileUpdateDto);
            return "redirect:/profile?error";
        }

        try {
            String email = customUserDetails.getUsername();
            userService.updateUserProfile(email, userProfileUpdateDto);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при обновлении профиля.");
        }

        return "redirect:/profile";
    }
}
