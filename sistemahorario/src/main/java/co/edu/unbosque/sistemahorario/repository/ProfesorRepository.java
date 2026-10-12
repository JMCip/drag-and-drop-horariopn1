package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Profesor;

public interface ProfesorRepository extends CrudRepository<Profesor, Long>{
	
	boolean existByNombre(String nombre);
	boolean existByCorreo(String correo);
}
