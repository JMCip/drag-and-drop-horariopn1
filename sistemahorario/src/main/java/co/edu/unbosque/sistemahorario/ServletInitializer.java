package co.edu.unbosque.sistemahorario;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Permite desplegar el back como WAR en un servidor de aplicaciones externo (por ejemplo Tomcat).
 * El proyecto se empaqueta como WAR (ver el pom.xml); esta clase le indica al contenedor
 * cuál es la clase principal de Spring Boot.
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * Registra {@link SistemahorarioApplication} como fuente de configuración cuando
	 * la aplicación arranca desde un contenedor de servlets.
	 *
	 * @param application constructor de la aplicación que entrega el contenedor
	 * @return el mismo constructor, ya configurado
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SistemahorarioApplication.class);
	}

}
