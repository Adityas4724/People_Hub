// ======================================================
// 1. Package Declaration
// ======================================================
package com.peoplehub.service;


// ======================================================
// 2. Import Statements
// ======================================================
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


// ======================================================
// 3. Email Service Class
// ======================================================
@Service
public class EmailService {

    // ==================================================
    // 4. JavaMailSender Dependency
    // ==================================================
    private final JavaMailSender mailSender;


    // ==================================================
    // 5. Constructor Injection
    // ==================================================
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    // ==================================================
    // 6. Send Welcome Email Method
    // ==================================================
    public void sendWelcomeEmail(
            String email,
            String employeeName,
            String username,
            String temporaryPassword) {

        // ==================================================
        // 7. Create Email Message
        // ==================================================
        SimpleMailMessage message = new SimpleMailMessage();


        // ==================================================
        // 8. Set Email Recipient and Subject
        // ==================================================
        message.setTo(email);
        message.setSubject("Welcome to PeopleHub!");


        // ==================================================
        // 9. Set Email Body
        // ==================================================
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


        // ==================================================
        // 10. Send Email
        // ==================================================
        mailSender.send(message);
    }
}