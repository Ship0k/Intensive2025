package aston.gateway.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/users")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.ok("USER-SERVICE временно недоступен. Попробуйте позже!");
    }

    @RequestMapping("/fallback/mail")
    public ResponseEntity<String> mailFallback() {
        return ResponseEntity.ok("NOTIFICATION-SERVICE недоступен. Письма не отправлены.");
    }
}