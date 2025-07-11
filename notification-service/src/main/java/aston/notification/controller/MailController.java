package aston.notification.controller;

import aston.notification.dto.UserEventDto;
import aston.notification.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody @Valid UserEventDto dto) {
        log.info("Получен запрос на отправку письма: [{}]", dto);
        String body = mailService.buildBody(dto.operation());
        try {
            mailService.send(dto.email().trim(), "Уведомление", body);
            return ResponseEntity.ok("Письмо успешно отправлено");
        } catch (Exception e) {
            log.error("Ошибка при отправке письма: [{}]", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Ошибка сервиса отправки почты");
        }
    }
}