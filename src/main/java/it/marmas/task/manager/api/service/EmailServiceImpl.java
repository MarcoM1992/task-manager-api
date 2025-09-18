package it.marmas.task.manager.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Base URLs for email links
    private static final String URL_RESET = "http://localhost:8080/auth/change_password";
    private static final String URL_ACTIVATION = "http://localhost:8080/auth/activate_account";
    private static final String TOKEN = "token";

    @Value("${spring.mail.password}")
    private String password; // Email password (used by mail sender)

    @Value("${spring.mail.username}")
    private String from; // Sender email address

    @Autowired
    private JavaMailSender mailSender; // Spring service for sending emails

    /**
     * Send a password reset email to a user.
     * @param emailTo recipient email address
     * @param token reset token
     * @param username recipient username
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendResetPasswordEmail(String emailTo, String token, String username) {
        try {
            String url = URL_RESET + "?" + TOKEN + "=" + token;
            String subject = "Password Reset Request";
            String content = loadResetPasswordTemplate(url, 2025, username);

            MimeMessage message = createMessage(emailTo, subject, content);
            mailSender.send(message);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

        logger.info("Password reset email successfully sent");
        return true;
    }

    /**
     * Send an account activation email to a new user.
     * @param emailTo recipient email
     * @param username recipient username
     * @param token activation token
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendActivationAccountEmail(String emailTo, String username, String token) {
        try {
            logger.info("Wrapping token in email: " + token);
            String url = URL_ACTIVATION + "?" + TOKEN + "=" + token;
            String subject = "Authentication Email";
            String content = loadActivationAccountTemplate(url, 2025, username);

            MimeMessage message = createMessage(emailTo, subject, content);
            mailSender.send(message);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

        logger.info("Activation email successfully sent");
        return true;
    }

    /**
     * Load the HTML template for activation email.
     * @param url activation link
     * @param year current year
     * @param username recipient username
     * @return HTML content as String
     * @throws IOException
     */
    private String loadActivationAccountTemplate(String url, int year, String username) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/activation-request.html");
        String html = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        return html.replace("{{username}}", username)
                   .replace("{{link}}", url)
                   .replace("{{year}}", "" + year);
    }

    /**
     * Load the HTML template for password reset email.
     * @param url reset link
     * @param year current year
     * @param username recipient username
     * @return HTML content as String
     * @throws IOException
     */
    private String loadResetPasswordTemplate(String url, int year, String username) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/password-reset.html");
        String html = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        return html.replace("{{resetUrl}}", url)
                   .replace("{{year}}", "" + year)
                   .replace("{{username}}", username);
    }

    /**
     * Utility to create a MIME message with HTML content and optional inline images.
     * @param emailTo recipient email
     * @param subject email subject
     * @param content HTML content
     * @return configured MimeMessage
     * @throws MessagingException
     */
    private MimeMessage createMessage(String emailTo, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setText(content, true);

        // Include inline logo image if referenced in content
        if (content.contains("logoImg")) {
            ClassPathResource img = new ClassPathResource("static/imgs/logo.png");
            helper.addInline("logoImg", img);
        }

        return message;
    }

    /**
     * Send a reminder email listing upcoming tasks for a user.
     * @param email recipient email
     * @param username recipient username
     * @param timezone recipient timezone (not used here but can be used for formatting)
     * @param taskList list of task descriptions
     */
    @Override
    public void sendReminderEmail(String email, String username, String timezone, List<String> taskList) {
        String subject = "Tasks Deadline";

        try {
            String content = loadReminderTemplate(taskList, username);
            MimeMessage message = createMessage(email, subject, content);
            mailSender.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }

        logger.info("Reminder email sent successfully");
    }

    /**
     * Load the HTML template for reminder emails listing tasks.
     * @param taskList list of task descriptions
     * @param username recipient username
     * @return HTML content as String
     * @throws IOException
     */
    private String loadReminderTemplate(List<String> taskList, String username) throws IOException {
        StringBuilder tasksHtml = new StringBuilder();
        for (String task : taskList) {
            tasksHtml.append("<li>").append(task).append("</li><br />");
        }

        ClassPathResource resource = new ClassPathResource("templates/reminder.html");
        String html = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        return html.replace("{{username}}", username)
                   .replace("{{tasks}}", tasksHtml.toString());
    }
}
