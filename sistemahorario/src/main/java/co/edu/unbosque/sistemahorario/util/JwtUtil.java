package co.edu.unbosque.sistemahorario.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.nio.charset.StandardCharsets; 

/**
 * Utilidades para crear y leer tokens JWT.
 * <p>
 * Los tokens se firman con HS256 usando {@code jwt.secret} y vencen según {@code jwt.expiration}
 * (milisegundos); ambos valores vienen de {@code application.properties}. El "subject" del token
 * es el correo del usuario.
 */
@Component
public class JwtUtil {

    /**
     * Clave secreta con la que se firman los tokens (propiedad {@code jwt.secret}). No debe subirse al repositorio.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Duración del token en milisegundos (propiedad {@code jwt.expiration}).
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * @param token token JWT
     * @return el correo del usuario guardado en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * @param token token JWT
     * @return la fecha de vencimiento del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Lee un dato (claim) del token con la función indicada.
     *
     * @param token          token JWT
     * @param claimsResolver función que elige qué dato leer
     * @param <T>            tipo del dato leído
     * @return el dato leído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Verifica la firma del token y devuelve todo su contenido. Lanza excepción si el token es inválido o fue alterado.
     *
     * @param token token JWT
     * @return los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token token JWT
     * @return {@code true} si ya venció
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token sin datos adicionales.
     *
     * @param userDetails usuario para el que se genera
     * @return el token firmado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token firmado con el correo como subject, la fecha de emisión y la de vencimiento.
     *
     * @param extraClaims datos adicionales a incluir en el token
     * @param userDetails usuario para el que se genera
     * @return el token firmado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Un token es válido si pertenece al usuario indicado y no ha vencido.
     *
     * @param token       token JWT
     * @param userDetails usuario contra el que se compara
     * @return {@code true} si es válido
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

 

 

    /**
     * Convierte el texto de {@code jwt.secret} en la clave HMAC de firma.
     *
     * @return la clave de firma
     */
    private Key getSignInKey() {
       byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
 }
}