package com.lendhand.app.lendhandservice.service;

import com.lendhand.app.lendhandservice.dto.UserRegistrationDto;
import com.lendhand.app.lendhandservice.entity.EmailVerificationToken;
import com.lendhand.app.lendhandservice.entity.User;
import com.lendhand.app.lendhandservice.exception.TokenExpiredException;
import com.lendhand.app.lendhandservice.exception.TokenNotFoundException;
import com.lendhand.app.lendhandservice.exception.UserAlreadyExistsException;
import com.lendhand.app.lendhandservice.exception.UserNotFoundException;
import com.lendhand.app.lendhandservice.repository.EmailVerificationTokenRepository;
import com.lendhand.app.lendhandservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // private final EmailService emailService;

    private static final int TOKEN_EXPIRATION_HOURS = 24;

    @Autowired
    public UserService(UserRepository userRepository, EmailVerificationTokenRepository emailVerificationTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с email: " + userRegistrationDto.getEmail() + " уже существует");
        }

        if (userRepository.existsByUsername(userRegistrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с username: " + userRegistrationDto.getUsername() + " уже существует");
        }

        User user = new User(
                userRegistrationDto.getUsername(),
                userRegistrationDto.getEmail(),
                passwordEncoder.encode(userRegistrationDto.getPassword())
        );

        user = userRepository.save(user);

        generateAndSendVerificationToken(user);

        return user;
    }

    private void generateAndSendVerificationToken(User user) {
        emailVerificationTokenRepository.findByUserAndConfirmedAtIsNull(user)
                .ifPresent(emailVerificationTokenRepository::delete);

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user, TOKEN_EXPIRATION_HOURS);
        emailVerificationTokenRepository.save(emailVerificationToken);

        // emailService.sendVerificationEmail(...)
    }

    public User verifyEmailToken(String tokenValue) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException("Токен подтверждения не найден."));

        if (emailVerificationToken.isExpired()) {
            throw new TokenExpiredException("Срок действия ссылки истек. Запросите повторную отправку письма подтверждения.");
        }

        if (emailVerificationToken.isConfirmed()) {
            throw new TokenExpiredException("Эта ссылка уже была использована для подтверждения аккаунта.");
        }

        User user = emailVerificationToken.getUser();
        user.verifyEmail();
        userRepository.save(user);

        emailVerificationToken.confirm();
        emailVerificationTokenRepository.save(emailVerificationToken);

        return user;
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + email + " не найден"));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email уже подтвержден.");
        }

        generateAndSendVerificationToken(user);
    }


    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
