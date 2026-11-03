package co.edu.unbosque.sistemahorario.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Materia;

/**
 * Acceso a datos de las materias (grupos). Spring Data genera la implementación a partir
 * de {@link CrudRepository} y de los nombres de los métodos.
 */
public interface MateriaRepository extends CrudRepository<Materia, Long>{
	
	/**
	 * Sirve para evitar grupos duplicados: la pareja nombre + grupo debe ser única.
	 *
	 * @param nombre nombre de la materia
	 * @param grupo  número de grupo
	 * @return {@code true} si ya existe ese grupo de esa materia
	 */
	boolean existsByNombreMateriaAndGrupo(String nombre, int grupo);
}
