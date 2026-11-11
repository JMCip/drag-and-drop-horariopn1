package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Materia;

/**
 * Acceso a datos de los grupos de materias. Spring Data genera la implementación a partir
 * de {@link CrudRepository} y de los nombres de los métodos.
 */
public interface MateriaRepository extends CrudRepository<Materia, Long>{
	
	/**
	 * Sirve para evitar grupos duplicados: la pareja asignatura + grupo debe ser única.
	 *
	 * @param idAsignatura identificador de la asignatura
	 * @param grupo        número de grupo
	 * @return {@code true} si ya existe ese grupo de esa asignatura
	 */
	boolean existsByAsignaturaIdAndGrupo(long idAsignatura, int grupo);
}
