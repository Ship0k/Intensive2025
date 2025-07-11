package aston.notification.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEventDto(
        @NotBlank(message = "Операция не может быть пустой")
        String operation,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email
) {}