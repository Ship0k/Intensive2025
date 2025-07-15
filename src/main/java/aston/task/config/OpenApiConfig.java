package aston.task.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Aston Task API",
                version = "1.0",
                description = "API для управления пользователями"
        ),
        servers = {
        @Server(url = "http://localhost:8080", description = "Локальный сервер")
}
)
public class OpenApiConfig {

}