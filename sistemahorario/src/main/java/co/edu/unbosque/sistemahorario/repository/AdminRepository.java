package co.edu.unbosque.sistemahorario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Admin;

/**
 * Acceso a datos de los administradores. Spring Data genera la implementación a partir
 * de {@link CrudRepository} y de los nombres de los métodos.
 */
public interface AdminRepository extends CrudRepository<Admin, Long>{

	/**
	 * @param correo correo a buscar
	 * @return {@code true} si algún administrador usa ese correo
	 */
	boolean existsByCorreo(String correo);
	/**
	 * @param correo correo a buscar
	 * @return el administrador con ese correo, si existe
	 */
	Optional<Admin> findByCorreo(String correo);
}
