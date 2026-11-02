package co.edu.unbosque.sistemahorario.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Admin;

public interface AdminRepository extends CrudRepository<Admin, Long>{

	boolean existsByCorreo(String correo);
	Optional<Admin> findByCorreo(String correo);
}
