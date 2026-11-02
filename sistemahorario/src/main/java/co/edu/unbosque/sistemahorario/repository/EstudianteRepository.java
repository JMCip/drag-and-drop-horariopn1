package co.edu.unbosque.sistemahorario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Estudiante;

public interface EstudianteRepository extends CrudRepository<Estudiante, Long>{

	boolean existsByNombre(String nombre);
	boolean existsByCorreo(String correo);
	Optional<Estudiante> findByCorreo(String correo);
}
