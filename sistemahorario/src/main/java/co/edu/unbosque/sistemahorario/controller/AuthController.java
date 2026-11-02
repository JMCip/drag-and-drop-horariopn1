package co.edu.unbosque.sistemahorario.controller;

import co.edu.unbosque.sistemahorario.dto.AuthResponse;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

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

        return ResponseEntity.ok(new AuthResponse(usuario.getId(), jwt, userDetailsService.obtenerRol(usuario),
                userDetailsService.obtenerNombre(usuario), usuario.getCorreo()));
    }
}
