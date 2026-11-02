package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Materia;

public interface MateriaRepository extends CrudRepository<Materia, Long>{
	
	boolean existsByNombreMateriaAndGrupo(String nombre, int grupo);
}
