package com.glabs.commonService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<div style=\"font-family: Arial, sans-serif;\">" +
                    "<h2 style=\"color: #4CAF50;\">Email Verification</h2>" +
                    "<p>Thank you for registering. Your verification code is:</p>" +
                    "<h3 style=\"color: #4CAF50;\">" + code + "</h3>" +
                    "<p>Please enter this code in the app to verify your email address.</p>" +
                    "<p>Thank you,</p>" +
                    "<p>Glabs</p>" +
                    "</div>";

            helper.setText(htmlMsg, true);
            helper.setTo(to);
            helper.setSubject("Email Verification");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
