package aston.notification.integration;

import aston.notification.dto.UserEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationIntegrationTest {

    TestRestTemplate restTemplate = new TestRestTemplate();
    RestTemplate mailhogClient = new RestTemplate();

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
            .withReuse(false);

    @Container
    @SuppressWarnings("resource")
    static GenericContainer<?> mailhog = new GenericContainer<>("mailhog/mailhog")
            .withExposedPorts(1025, 8025).withReuse(false);

    @DynamicPropertySource
    static void mailhogProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeEach
    void clearMailhog() {
        mailhogClient.delete("http://localhost:" + mailhog.getMappedPort(8025) + "/api/v1/messages");
    }

    @LocalServerPort
    int port;

    @Test
    void sendNotification_shouldReturn200_andTriggerEmail() throws Exception {

        String baseUrl = "http://localhost:" + port + "/api/mail/send";
        UserEventDto dto = new UserEventDto("CREATE", "mailhog@local.test");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserEventDto> request = new HttpEntity<>(dto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Письмо успешно отправлено");

        String mailhogUrl = "http://localhost:" + mailhog.getMappedPort(8025) + "/api/v2/messages";
        ResponseEntity<String> mailhogResponse = mailhogClient.getForEntity(mailhogUrl, String.class);
        assertThat(mailhogResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(mailhogResponse.getBody());
        JsonNode item = root.path("items").get(0);

        String toHeader = item.path("Content").path("Headers").path("To").get(0).asText();
        assertThat(toHeader).isEqualTo("mailhog@local.test");

        String encodedBody = item.path("Content").path("Body").asText();
        String cleanBody = encodedBody.replaceAll("\\s+", "");
        String decodedBody = new String(Base64.getDecoder().decode(cleanBody), StandardCharsets.UTF_8);

        assertThat(decodedBody).contains("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
    }
}