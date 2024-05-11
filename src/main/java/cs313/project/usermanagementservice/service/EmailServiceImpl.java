package cs313.project.usermanagementservice.service;

import cs313.project.usermanagementservice.service.interfaceservice.IEmail;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmail {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public Boolean sendMail(String recipient, String username, String otp)
    {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        String formattedDate = currentDate.format(formatter);

        // Try block to check for exceptions
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setTo(recipient);
            helper.setSubject("Registration Confirmation");

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("otp", otp);
            context.setVariable("date", formattedDate);

            String emailContent = templateEngine.process("verify_email", context);
            helper.setText(emailContent, true);

            // Sending the mail
            javaMailSender.send(mailMessage);
            return true;
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return false;
        }
    }
}
