package co.edu.unbosque.sistemahorario.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;



/**
 * Configuración de seguridad de la API.
 * <p>
 * Define qué rutas son públicas y cuáles exigen un rol, desactiva las sesiones (la autenticación
 * es solo por token JWT) y registra el filtro {@link JwtRequestFilter}. También declara los
 * beans de cifrado de contraseñas (BCrypt) y de autenticación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Arma la cadena de filtros de seguridad.
     * <ul>
     * <li>Públicas: login, Swagger y {@code /error}.</li>
     * <li>Cualquier usuario autenticado: listar materias y profesores.</li>
     * <li>ADMIN y PROFESOR: listar estudiantes.</li>
     * <li>ADMIN y ESTUDIANTE: inscribir y desinscribir (el estudiante solo sobre sí mismo, lo valida {@code MateriaController}).</li>
     * <li>Solo ADMIN: el resto de {@code /admin}, {@code /estudiante}, {@code /profesor}, {@code /materia} y {@code /asignatura}.</li>
     * </ul>
     * Sin token o con token vencido se responde 401. CSRF está desactivado porque no hay sesiones.
     *
     * @param http constructor de la configuración HTTP
     * @return la cadena de filtros ya construida
     * @throws Exception si la configuración falla
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Usa el bean corsConfigurationSource de CorsConfig
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF ya que usamos tokens JWT
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (ajusta según los endpoints de login/registro de tu proyecto)
                // /error debe ser publico: si no, un 400/404 del back llega al front como 401
                .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                // Consultas que usan las vistas de estudiante y profesor
                .requestMatchers(HttpMethod.GET, "/materia/mostrartodo", "/profesor/mostrartodo").authenticated()
                .requestMatchers(HttpMethod.GET, "/estudiante/mostrartodo").hasAnyRole("ADMIN", "PROFESOR")
                // Un estudiante solo opera sobre si mismo: lo valida MateriaController
                .requestMatchers("/materia/inscribirestudiante", "/materia/desinscribirestudiante").hasAnyRole("ADMIN", "ESTUDIANTE")
                // Todo lo demas de estos modulos es de la Secretaria Academica
                .requestMatchers("/admin/**", "/estudiante/**", "/profesor/**", "/materia/**", "/asignatura/**").hasRole("ADMIN")
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT no maneja sesiones de HttpSession
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // Sin token o vencido: 401 y no 403
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proveedor que autentica buscando al usuario con {@link UserDetailsService} y
     * comparando la contraseña con BCrypt.
     *
     * @return el proveedor de autenticación
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Codificador de contraseñas. BCrypt guarda un hash con sal, por lo que no se puede revertir.
     *
     * @return el codificador BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Administrador de autenticación que usa {@code AuthController} para validar el login.
     *
     * @param config configuración de autenticación de Spring
     * @return el administrador de autenticación
     * @throws Exception si no se puede obtener
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}