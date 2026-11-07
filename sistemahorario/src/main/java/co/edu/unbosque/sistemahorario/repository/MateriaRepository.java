package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Materia;

/**
 * Acceso a datos de las materias (grupos). Spring Data genera la implementación a partir
 * de {@link CrudRepository} y de los nombres de los métodos.
 */
public interface MateriaRepository extends CrudRepository<Materia, Long>{
	
	boolean existsByAsignaturaIdAndGrupo(long idAsignatura, int grupo);
}
