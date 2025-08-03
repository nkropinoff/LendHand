package com.lendhand.app.lendhandservice.repository;

import com.lendhand.app.lendhandservice.entity.EmailVerificationToken;
import com.lendhand.app.lendhandservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUserAndConfirmedAtIsNull(User user);
}
