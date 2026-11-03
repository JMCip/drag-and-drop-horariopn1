package co.edu.unbosque.sistemahorario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Estudiante;

/**
 * Acceso a datos de los estudiantes. Spring Data genera la implementación a partir
 * de {@link CrudRepository} y de los nombres de los métodos.
 */
public interface EstudianteRepository extends CrudRepository<Estudiante, Long>{

	/**
	 * @param nombre nombre a buscar
	 * @return {@code true} si algún estudiante tiene ese nombre
	 */
	boolean existsByNombre(String nombre);
	/**
	 * @param correo correo a buscar
	 * @return {@code true} si algún estudiante usa ese correo
	 */
	boolean existsByCorreo(String correo);
	/**
	 * @param correo correo a buscar
	 * @return el estudiante con ese correo, si existe
	 */
	Optional<Estudiante> findByCorreo(String correo);
}
