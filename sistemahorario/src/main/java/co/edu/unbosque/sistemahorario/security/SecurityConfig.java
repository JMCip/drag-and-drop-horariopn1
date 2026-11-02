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



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UserDetailsService userDetailsService;

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
                .requestMatchers("/admin/**", "/estudiante/**", "/profesor/**", "/materia/**").hasRole("ADMIN")
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

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}