package aston.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    MailService mailService;
    @InjectMocks
    KafkaConsumer consumer;

    @Test
    void handleUserEvent_shouldSendEmailForCreate() {
        String message = "Операция:CREATE,Email:test@test.com";
        when(mailService.buildBody("CREATE"))
                .thenReturn("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
        consumer.handleUserEvent(message);
        verify(mailService).send("test@test.com", "Уведомление",
                "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
    }

    @Test
    void handleUserEvent_shouldIgnoreEmptyMessage() {
        consumer.handleUserEvent("   ");
        verifyNoInteractions(mailService);
    }

    @Test
    void handleUserEvent_shouldWarnOnInvalidFormat() {
        consumer.handleUserEvent("invalid-format");
        verifyNoInteractions(mailService);
    }
}