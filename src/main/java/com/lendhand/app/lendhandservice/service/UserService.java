package com.lendhand.app.lendhandservice.service;

import com.lendhand.app.lendhandservice.dto.UserProfileUpdateDto;
import com.lendhand.app.lendhandservice.dto.UserRegistrationDto;
import com.lendhand.app.lendhandservice.entity.EmailVerificationToken;
import com.lendhand.app.lendhandservice.entity.User;
import com.lendhand.app.lendhandservice.entity.UserProfile;
import com.lendhand.app.lendhandservice.exception.EmailAlreadyExistsException;
import com.lendhand.app.lendhandservice.exception.TokenExpiredException;
import com.lendhand.app.lendhandservice.exception.TokenNotFoundException;
import com.lendhand.app.lendhandservice.exception.UsernameAlreadyExistsException;
import com.lendhand.app.lendhandservice.repository.EmailVerificationTokenRepository;
import com.lendhand.app.lendhandservice.repository.UserProfileRepository;
import com.lendhand.app.lendhandservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private static final int TOKEN_EXPIRATION_HOURS = 24;

    @Autowired
    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, EmailVerificationTokenRepository emailVerificationTokenRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User registerUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Пользователь с электронной почтой " + userRegistrationDto.getEmail() + " уже существует.");
        }

        if (userRepository.existsByUsername(userRegistrationDto.getUsername())) {
            throw new UsernameAlreadyExistsException("Пользователь с именем " + userRegistrationDto.getUsername() + " уже существует.");
        }

        User user = new User(
                userRegistrationDto.getUsername(),
                userRegistrationDto.getEmail(),
                passwordEncoder.encode(userRegistrationDto.getPassword())
        );

        user = userRepository.save(user);

        UserProfile userProfile = new UserProfile(user);
        userProfileRepository.save(userProfile);

        generateAndSendVerificationToken(user);

        return user;
    }

    private void generateAndSendVerificationToken(User user) {
        emailVerificationTokenRepository.findByUserAndConfirmedAtIsNull(user)
                .ifPresent(emailVerificationTokenRepository::delete);

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user, TOKEN_EXPIRATION_HOURS);
        emailVerificationTokenRepository.save(emailVerificationToken);

        emailService.sendVerificationEmail(user, emailVerificationToken.getToken());
    }

    public User verifyEmailToken(String tokenValue) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException("Токен подтверждения не найден."));

        if (emailVerificationToken.isExpired()) {
            emailVerificationTokenRepository.delete(emailVerificationToken);
            throw new TokenExpiredException("Срок действия ссылки истек. Запросите повторную отправку письма подтверждения.");
        }

        if (emailVerificationToken.isConfirmed()) {
            throw new IllegalStateException("Эта ссылка уже была использована для подтверждения аккаунта.");
        }

        User user = emailVerificationToken.getUser();
        user.verifyEmail();
        userRepository.save(user);

        emailVerificationToken.confirm();
        emailVerificationTokenRepository.save(emailVerificationToken);

        return user;
    }

    @Async
    public void requestResendVerificationEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        userOptional.ifPresent(user -> {
            if (!user.isEmailVerified()) generateAndSendVerificationToken(user);
        });
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Пользователь с email: " + email + " не найден"));
    }

    public void updateUserProfile(String email, UserProfileUpdateDto userProfileUpdateDto) {
        User user = findUserByEmail(email);
        UserProfile userProfile = user.getUserProfile();

        userProfile.setLocation(userProfileUpdateDto.getLocation());
        userProfile.setAbout(userProfileUpdateDto.getAbout());

        userProfileRepository.save(userProfile);
    }

    public void updateUserAvatar(String email, String avatarURL) {
        User user = findUserByEmail(email);
        UserProfile userProfile = user.getUserProfile();
        userProfile.setAvatarUrl(avatarURL);
        userProfileRepository.save(userProfile);
    }
}
