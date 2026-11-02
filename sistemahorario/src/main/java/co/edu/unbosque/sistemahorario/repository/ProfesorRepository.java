package co.edu.unbosque.sistemahorario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Profesor;

public interface ProfesorRepository extends CrudRepository<Profesor, Long>{
	
	boolean existsByNombre(String nombre);
	boolean existsByCorreo(String correo);
	Optional<Profesor> findByCorreo(String correo);
}
