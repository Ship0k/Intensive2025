package aston.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    static final String CREATE = "CREATE";
    static final String DELETE = "DELETE";

    public void send(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            log.info("Письмо успешно отправлено на адрес: {}", to);

        } catch (Exception e) {
            log.error("Ошибка отправки письма на адрес: {} — {}", to, e.getMessage(), e);
            throw new IllegalStateException("Ошибка отправки письма: " + e.getMessage(), e);
        }
    }

    public String buildBody(String operation) {
        return switch (operation.trim().toUpperCase()) {
            case CREATE -> "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
            case DELETE -> "Здравствуйте! Ваш аккаунт был удалён.";
            default -> {
                log.warn("Неизвестная операция: [{}]", operation);
                yield "Неизвестная операция.";
            }
        };
    }
}