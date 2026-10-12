package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Estudiante;

public interface EstudianteRepository extends CrudRepository<Estudiante, Long>{

	boolean existByNombre(String nombre);
	boolean existByCorreo(String correo);
}
