package co.edu.unbosque.sistemahorario.security;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Configuración de Swagger: agrega el botón <b>Authorize</b> para pegar el token JWT y
 * probar los endpoints protegidos desde {@code /swagger-ui.html}.
 */
// Agrega a Swagger el boton Authorize para pegar el token del login
@Configuration
@OpenAPIDefinition(security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
