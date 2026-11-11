package co.edu.unbosque.sistemahorario.controller;

import co.edu.unbosque.sistemahorario.dto.AuthResponse;
import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Usuario;
import co.edu.unbosque.sistemahorario.service.UserDetailsServiceImpl;
import co.edu.unbosque.sistemahorario.util.JwtUtil; // Ajusta según el nombre de tu paquete de utilidades
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación ({@code /api/auth}).
 * <p>
 * Es la única puerta pública de la API: valida el correo y la contraseña y, si son correctos,
 * entrega un token JWT que el cliente debe enviar en la cabecera
 * {@code Authorization: Bearer <token>} en las demás peticiones.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Inicia sesión y genera el token JWT.
     * <p>
     * Flujo: autentica con {@link AuthenticationManager} (compara la contraseña contra el hash BCrypt),
     * busca al usuario para conocer su rol y su nombre, genera el token y lo devuelve junto con esos datos.
     * Si el usuario es un estudiante, la respuesta incluye además su carrera y su semestre.
     *
     * @param correo     correo del usuario
     * @param contrasenia contraseña en texto plano (se compara contra el hash guardado)
     * @return 200 con un {@link AuthResponse} (id, token, rol, nombre, correo y, para estudiantes, carrera y semestre);
     *         401 si las credenciales no son válidas
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestParam String correo, @RequestParam String contrasenia) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, contrasenia)
            );
        } catch (AuthenticationException e) {
            return new ResponseEntity<String>("Correo o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
        final String jwt = jwtUtil.generateToken(userDetails);
        final Usuario usuario = userDetailsService.buscarPorCorreo(correo);

        AuthResponse respuesta = new AuthResponse(usuario.getId(), jwt, userDetailsService.obtenerRol(usuario),
                userDetailsService.obtenerNombre(usuario), usuario.getCorreo());
        if (usuario instanceof Estudiante) {
            Estudiante est = (Estudiante) usuario;
            respuesta.setCarrera(est.getCarrera() != null ? est.getCarrera().name() : null);
            respuesta.setSemestre(est.getSemestre());
        }
        return ResponseEntity.ok(respuesta);
    }
}
