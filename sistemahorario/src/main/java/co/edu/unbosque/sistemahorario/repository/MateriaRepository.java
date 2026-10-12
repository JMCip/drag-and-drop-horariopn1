package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Materia;

public interface MateriaRepository extends CrudRepository<Materia, Long>{
	
	boolean existByNombreMateria(String nombre);
}
