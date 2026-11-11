package co.edu.unbosque.sistemahorario.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de CORS: define qué páginas web pueden llamar a la API desde el navegador.
 */
@Configuration
public class CorsConfig {

    /**
     * Permite peticiones únicamente desde el front en desarrollo ({@code http://localhost:4200}),
     * con los métodos GET, POST, PUT, DELETE y OPTIONS y las cabeceras {@code Authorization} y {@code Content-Type}.
     * Si el front se despliega en otra URL, hay que agregarla aquí.
     *
     * @return la fuente de configuración CORS aplicada a todas las rutas
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
