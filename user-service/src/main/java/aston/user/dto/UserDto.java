package aston.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
        Long id,

        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @Email(message = "Некорректный email")
        @NotBlank(message = "Email обязателен")
        String email,

        @Min(value = 0, message = "Возраст должен быть неотрицательным")
        int age
) {}