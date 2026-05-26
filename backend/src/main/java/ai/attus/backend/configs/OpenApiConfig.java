package ai.attus.backend.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
            new Info()
                .title("Task Management API")
                .version("1.0.0")
                .description(
                    "API REST para gerenciamento de tarefas com suporte a criação, atualização, listagem e controle de status."
                )
                .contact(
                    new Contact()
                        .name("Matheus")
                        .email("reisdev.matheus@gmail.com")
                        .url("https://wa.me/5519991045415")
                )
                .license(
                    new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                )
        );
    }
}
