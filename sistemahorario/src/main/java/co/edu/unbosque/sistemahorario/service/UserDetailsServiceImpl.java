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

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepository admRepo;

    @Autowired
    private ProfesorRepository profRepo;

    @Autowired
    private EstudianteRepository estRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = buscarPorCorreo(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return new User(usuario.getCorreo(), usuario.getContrasenia(),
                List.of(new SimpleGrantedAuthority("ROLE_" + obtenerRol(usuario))));
    }

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

    public String obtenerRol(Usuario usuario) {
        if (usuario instanceof Admin) {
            return "ADMIN";
        }
        return usuario instanceof Profesor ? "PROFESOR" : "ESTUDIANTE";
    }

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
