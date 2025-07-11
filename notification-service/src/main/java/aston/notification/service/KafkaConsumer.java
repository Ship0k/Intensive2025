package aston.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "${kafka.topic.user-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserEvent(String message) {
        log.info("Получено сообщение из Kafka: [{}]", message);

        if (message == null || message.trim().isBlank()) {
            log.warn("Сообщение пустое — игнорируем");
            return;
        }

        try {
            String[] parts = message.split(",");
            String operation = extractValue(parts, "Операция");
            String email = extractValue(parts, "Email");

            if (operation == null || operation.trim().isBlank() || email == null || email.trim().isBlank()) {
                log.warn("Не удалось извлечь email или operation из Kafka-сообщения: [{}]", message);
                return;
            }

            String body = mailService.buildBody(operation);
            mailService.send(email.trim(), "Уведомление", body);

        } catch (Exception e) {
            log.error("Ошибка при обработке Kafka-сообщения: [{}]", message, e);
        }
    }

    private String extractValue(String[] parts, String key) {
        for (String part : parts) {
            String[] kv = part.split(":");
            if (kv.length == 2 && key.equalsIgnoreCase(kv[0].trim())) {
                return kv[1].trim();
            }
        }
        return null;
    }
}