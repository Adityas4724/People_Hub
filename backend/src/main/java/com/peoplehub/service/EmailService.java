
package com.peoplehub.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(
            String email,
            String employeeName,
            String username,
            String temporaryPassword) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Welcome to PeopleHub!");

        message.setText(
            "Hello " + employeeName + ",\n\n" +
            "Welcome to PeopleHub!\n\n" +
            "Your employee account has been successfully created.\n\n" +
            "Login Details:\n" +
            "Username: " + username + "\n" +
            "Temporary Password: " + temporaryPassword + "\n\n" +
            "Please log in and change your password.\n\n" +
            "Best Regards,\n" +
            "PeopleHub Admin Team"
        );

        mailSender.send(message);
    }
}
