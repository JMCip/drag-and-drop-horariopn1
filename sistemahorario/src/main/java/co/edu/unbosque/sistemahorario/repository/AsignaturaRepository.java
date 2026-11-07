package co.edu.unbosque.sistemahorario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.sistemahorario.entity.Asignatura;

public interface AsignaturaRepository extends CrudRepository<Asignatura, Long>{

	// Una sola consulta: con la base remota, cargar los planes uno por uno tardaba ~35 s
	@Query("select distinct a from Asignatura a left join fetch a.planes")
	List<Asignatura> findAllConPlanes();
}
