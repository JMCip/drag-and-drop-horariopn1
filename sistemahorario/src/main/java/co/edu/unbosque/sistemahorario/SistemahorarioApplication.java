package co.edu.unbosque.sistemahorario;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Clase principal del back del Sistema de Horarios.
 * <p>
 * Al ejecutarla se levanta el servidor Spring Boot (puerto 8080 por defecto), se conecta
 * a la base de datos configurada en {@code application.properties} y quedan expuestos los
 * endpoints REST y la documentación Swagger.
 */
@SpringBootApplication
public class SistemahorarioApplication {

	/**
	 * Punto de entrada de la aplicación.
	 *
	 * @param args argumentos de línea de comandos (no se usan)
	 */
	public static void main(String[] args) {
		SpringApplication.run(SistemahorarioApplication.class, args);
	}

	/**
	 * Crea el {@link ModelMapper} que usan los servicios para convertir entre entidades y DTO.
	 * Al ser un bean, Spring lo inyecta con {@code @Autowired} donde se necesite.
	 *
	 * @return una instancia de ModelMapper con la configuración por defecto
	 */
	@Bean
	public ModelMapper getModelMapper() {
		
		return new ModelMapper();
	}
}
