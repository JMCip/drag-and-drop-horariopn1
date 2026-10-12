package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Admin;

public interface AdminRepository extends CrudRepository<Admin, Long>{

	boolean existByCorreo(String correo);
}
