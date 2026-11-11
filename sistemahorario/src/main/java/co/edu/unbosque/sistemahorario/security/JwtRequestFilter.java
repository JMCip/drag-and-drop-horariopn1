package co.edu.unbosque.sistemahorario.security;

import co.edu.unbosque.sistemahorario.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta una vez por petición para leer el token JWT.
 * <p>
 * Si la cabecera {@code Authorization: Bearer <token>} trae un token válido, deja al usuario
 * autenticado en el contexto de seguridad; si no, la petición sigue sin autenticar y
 * {@link SecurityConfig} decide si la rechaza con 401.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Extrae el token de la cabecera, valida que sea del usuario y no esté vencido, y registra
     * la autenticación. Si el usuario del token ya fue eliminado, la petición queda sin autenticar (401).
     *
     * @param request     petición entrante
     * @param response    respuesta
     * @param filterChain siguiente eslabón de la cadena
     * @throws ServletException si falla el filtrado
     * @throws IOException      si falla la lectura o escritura
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("No se pudo extraer el username del token JWT", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (UsernameNotFoundException e) {
                // Token valido de un usuario ya eliminado: se responde 401, no 500
                logger.warn("El usuario del token JWT ya no existe: " + username);
            }
        }

        filterChain.doFilter(request, response);
    }
}