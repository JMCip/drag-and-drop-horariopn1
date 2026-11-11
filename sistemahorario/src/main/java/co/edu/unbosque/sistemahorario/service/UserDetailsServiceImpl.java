package co.edu.unbosque.sistemahorario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.sistemahorario.entity.Admin;
import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.entity.Usuario;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;

/**
 * Conecta Spring Security con las tablas de usuarios del sistema.
 * <p>
 * Como los usuarios están repartidos en tres tablas (administradores, profesores y
 * estudiantes) y se identifican por su correo, este servicio busca en las tres y arma el
 * {@link UserDetails} con el rol correspondiente ({@code ROLE_ADMIN}, {@code ROLE_PROFESOR}
 * o {@code ROLE_ESTUDIANTE}).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepository admRepo;

    @Autowired
    private ProfesorRepository profRepo;

    @Autowired
    private EstudianteRepository estRepo;

    /**
     * Carga un usuario por su correo para que Spring Security lo autentique.
     *
     * @param username correo del usuario
     * @return datos del usuario con su hash de contraseña y su rol
     * @throws UsernameNotFoundException si el correo no existe en ninguna de las tres tablas
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = buscarPorCorreo(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return new User(usuario.getCorreo(), usuario.getContrasenia(),
                List.of(new SimpleGrantedAuthority("ROLE_" + obtenerRol(usuario))));
    }

    /**
     * Busca un usuario por correo en administradores, profesores y estudiantes, en ese orden.
     *
     * @param correo correo a buscar
     * @return el usuario encontrado, o {@code null} si no existe
     */
    public Usuario buscarPorCorreo(String correo) {
        Usuario usuario = admRepo.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            usuario = profRepo.findByCorreo(correo).orElse(null);
        }
        if (usuario == null) {
            usuario = estRepo.findByCorreo(correo).orElse(null);
        }
        return usuario;
    }

    /**
     * Determina el rol de un usuario según su clase.
     *
     * @param usuario usuario ya cargado
     * @return {@code "ADMIN"}, {@code "PROFESOR"} o {@code "ESTUDIANTE"}
     */
    public String obtenerRol(Usuario usuario) {
        if (usuario instanceof Admin) {
            return "ADMIN";
        }
        return usuario instanceof Profesor ? "PROFESOR" : "ESTUDIANTE";
    }

    /**
     * Obtiene el nombre a mostrar de un usuario.
     *
     * @param usuario usuario ya cargado
     * @return el nombre del profesor o estudiante; para un administrador, que no tiene nombre, el texto {@code "Administrador"}
     */
    public String obtenerNombre(Usuario usuario) {
        if (usuario instanceof Profesor) {
            return ((Profesor) usuario).getNombre();
        }
        if (usuario instanceof Estudiante) {
            return ((Estudiante) usuario).getNombre();
        }
        return "Administrador";
    }
}
