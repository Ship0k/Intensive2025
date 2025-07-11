package aston.notification.integration;

import aston.notification.service.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaListenerIntegrationTest {

    private static final String TOPIC = "user-events";

    @SpyBean
    MailService mailService;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldProcessCreateEventAndSendEmail() {
        String message = "Операция:CREATE,Email:test@test.com";
        kafkaTemplate.send(TOPIC, message);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                verify(mailService).send(eq("test@test.com"),
                        eq("Уведомление"),
                        eq("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."))
        );
    }

    @Test
    void shouldIgnoreInvalidKafkaMessage() {
        kafkaTemplate.send(TOPIC, "invalid-format");
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() ->
                verify(mailService, never()).send(any(), any(), any())
        );
    }

    @Test
    void shouldProcessDeleteEventAndSendEmail() {
        String message = "Операция:DELETE,Email:test@test.com";
        kafkaTemplate.send(TOPIC, message);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                verify(mailService).send(eq("test@test.com"),
                        eq("Уведомление"),
                        eq("Здравствуйте! Ваш аккаунт был удалён."))
        );
    }
}