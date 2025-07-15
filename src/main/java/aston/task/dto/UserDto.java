package aston.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO пользователя, содержащий основные данные")
public record UserDto(
        @Schema(description = "Идентификатор пользователя", example = "1")
        Long id,

        @Schema(description = "Имя пользователя", example = "Анатолий")
        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @Schema(description = "Email пользователя", example = "tolik@mail.com")
        @Email(message = "Некорректный email")
        String email,

        @Schema(description = "Возраст пользователя (неотрицательное число)", example = "35", minimum = "0")
        @Min(value = 0, message = "Возраст должен быть неотрицательным")
        int age
) {}