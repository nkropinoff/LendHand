package com.lendhand.app.lendhandservice.service;

import com.lendhand.app.lendhandservice.entity.User;
import com.lendhand.app.lendhandservice.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String senderEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, @Value("${spring.mail.username}") String senderEmail) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.senderEmail = senderEmail;
    }

    @Async
    public void sendVerificationEmail(User user, String token) {
        try {
            String verificationUrl = "http://localhost:8080/verification?token=" + token;

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("verificationUrl", verificationUrl);

            String htmlBody = templateEngine.process("email/verification-email", context);
            sendHtmlEmail(user.getEmail(), "Подтверждение регистрации на LendHand", htmlBody);

        } catch (Exception e) {
            throw new EmailSendingException("Не удалось отправить письмо для подтверждения email", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        helper.setTo(to);
        helper.setFrom(senderEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(mimeMessage);
    }
}
