package com.lendhand.app.lendhandservice.controller;

import com.lendhand.app.lendhandservice.dto.UserProfileUpdateDto;
import com.lendhand.app.lendhandservice.entity.User;
import com.lendhand.app.lendhandservice.entity.UserProfile;
import com.lendhand.app.lendhandservice.service.CustomUserDetails;
import com.lendhand.app.lendhandservice.service.FileStorageService;
import com.lendhand.app.lendhandservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Autowired
    ProfileController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String profilePage(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String email = customUserDetails.getUsername();
        User user = userService.findUserByEmail(email);
        UserProfile userProfile = user.getUserProfile();

        model.addAttribute("user", user);
        model.addAttribute("userProfile", userProfile);

        if (!model.containsAttribute("profileUpdateDto")) {
            UserProfileUpdateDto userProfileUpdateDto = new UserProfileUpdateDto();
            if (userProfile != null) {
                userProfileUpdateDto.setLocation(userProfile.getLocation());
                userProfileUpdateDto.setAbout(userProfile.getAbout());
            }
            model.addAttribute("profileUpdateDto", userProfileUpdateDto);
        }
        return "profile";
    }

    @PostMapping("/update/details")
    public String updateProfileDetails(@Valid @ModelAttribute("profileUpdateDto") UserProfileUpdateDto userProfileUpdateDto,
                                BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileUpdateDto", bindingResult);
            redirectAttributes.addFlashAttribute("profileUpdateDto", userProfileUpdateDto);
            return "redirect:/profile?error=details";
        }

        try {
            Long userId = customUserDetails.getUserId();
            User updatedUser = userService.updateUserProfile(userId, userProfileUpdateDto);
            updatePrincipal(updatedUser);

            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при обновлении деталей профиля.");
        }

        return "redirect:/profile";
    }

    private void updatePrincipal(User updatedUser) {
        CustomUserDetails newPrincipal = new CustomUserDetails(updatedUser);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                newPrincipal,
                authentication.getCredentials(),
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @PostMapping("/update/avatar")
    public String updateProfileAvatar(@RequestParam("avatarFile") MultipartFile avatarFile,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      RedirectAttributes redirectAttributes) {

        if (avatarFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пожалуйста, выберите файл для загрузки.");
            return "redirect:/profile?error=avatar";
        }

        try {
            Long userId = customUserDetails.getUserId();
            User user = userService.findUserById(userId);

            String oldAvatarUrl = user.getUserProfile().getAvatarUrl();
            fileStorageService.deleteFileByUrl(oldAvatarUrl);

            String objectName = fileStorageService.uploadFile(avatarFile);
            String newAvatarUrl = fileStorageService.buildFileUrl(objectName);
            User updatedUser =  userService.updateUserAvatar(userId, newAvatarUrl);
            updatePrincipal(updatedUser);

            redirectAttributes.addFlashAttribute("successMessage", "Аватар профиля успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при обновлении аватара профиля.");
        }

        return "redirect:/profile";
    }

}
