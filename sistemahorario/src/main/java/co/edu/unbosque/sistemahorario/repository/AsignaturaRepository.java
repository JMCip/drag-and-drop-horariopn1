package co.edu.unbosque.sistemahorario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Asignatura;

/**
 * Acceso a datos del catálogo de asignaturas. Spring Data genera la implementación a partir
 * de {@link CrudRepository}.
 */
public interface AsignaturaRepository extends CrudRepository<Asignatura, Long>{

	/**
	 * Trae todas las asignaturas junto con sus planes en una sola consulta ({@code left join fetch}).
	 * Con la base remota, cargar los planes de uno en uno tardaba unos 35 segundos.
	 *
	 * @return lista de asignaturas con sus planes ya cargados
	 */
	// Una sola consulta: con la base remota, cargar los planes uno por uno tardaba ~35 s
	@Query("select distinct a from Asignatura a left join fetch a.planes")
	List<Asignatura> findAllConPlanes();
}
