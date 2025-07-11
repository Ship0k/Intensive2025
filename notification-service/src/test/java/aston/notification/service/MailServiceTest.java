package aston.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    JavaMailSender mailSender;
    @Mock
    MimeMessage mimeMessage;
    @InjectMocks
    MailService mailService;

    @Test
    void send_shouldCallJavaMailSender() throws MessagingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.send("test@test.com", "Test Subject", "Hello!");
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void send_shouldThrowIllegalStateException_whenSendingFails() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(MimeMessage.class));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                mailService.send("test@test.com", "Test Subject", "Hello!"));
        assertTrue(exception.getMessage().contains("Ошибка отправки"));
    }

    @Test
    void buildBody_shouldReturnCreateMessage() {
        String result = mailService.buildBody("CREATE");
        assertEquals("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.", result);
    }

    @Test
    void buildBody_shouldReturnDeleteMessage() {
        String result = mailService.buildBody("DELETE");
        assertEquals("Здравствуйте! Ваш аккаунт был удалён.", result);
    }

    @Test
    void buildBody_shouldReturnFallbackMessage() {
        String result = mailService.buildBody("UNKNOWN");
        assertEquals("Неизвестная операция.", result);
    }
}