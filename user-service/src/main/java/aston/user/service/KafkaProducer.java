package aston.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import aston.user.exception.UserServiceException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public void sendUserEvent(String eventType, String email) {
        String message = String.format("Операция: %s, Email: %s", eventType, email);

        log.info("Отправка Kafka-сообщения: {}", message);
        try {
            kafkaTemplate.send(TOPIC, message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Ошибка отправки сообщения в Kafka: {}", ex.getMessage(), ex);
                        } else {
                            log.info("Сообщение успешно отправлено в Kafka. Partition: {}, Offset: {}",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (KafkaException e) {
            log.error("Фатальная ошибка Kafka-продюсера при отправке сообщения", e);
            throw new UserServiceException("Ошибка при отправке события в Kafka", e);
        }
    }
}