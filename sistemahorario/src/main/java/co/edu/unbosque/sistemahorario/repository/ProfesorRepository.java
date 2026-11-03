package co.edu.unbosque.sistemahorario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Profesor;

/**
 * Acceso a datos de los profesores. Spring Data genera la implementación a partir
 * de {@link CrudRepository} y de los nombres de los métodos.
 */
public interface ProfesorRepository extends CrudRepository<Profesor, Long>{
	
	/**
	 * @param nombre nombre a buscar
	 * @return {@code true} si algún profesor tiene ese nombre
	 */
	boolean existsByNombre(String nombre);
	/**
	 * @param correo correo a buscar
	 * @return {@code true} si algún profesor usa ese correo
	 */
	boolean existsByCorreo(String correo);
	/**
	 * @param correo correo a buscar
	 * @return el profesor con ese correo, si existe
	 */
	Optional<Profesor> findByCorreo(String correo);
}
