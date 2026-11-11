package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.sistemahorario.dto.AsignaturaDTO;
import co.edu.unbosque.sistemahorario.dto.PlanEstudioDTO;
import co.edu.unbosque.sistemahorario.entity.Asignatura;
import co.edu.unbosque.sistemahorario.entity.PlanEstudio;
import co.edu.unbosque.sistemahorario.repository.AsignaturaRepository;

/**
 * Consulta del catálogo de asignaturas. Es de solo lectura ({@code readOnly = true}): el catálogo
 * se carga por SQL ({@code catalogo_materias.sql}) y desde la aplicación no se crea ni se edita.
 */
// El catalogo se carga por SQL (catalogo_materias.sql); aqui solo se consulta
@Service
@Transactional(readOnly = true)
public class AsignaturaService {

	@Autowired
	private AsignaturaRepository asigRepo;

	/**
	 * Lista todo el catálogo, ordenado por nombre sin distinguir mayúsculas.
	 *
	 * @return lista de {@link AsignaturaDTO} con sus planes
	 */
	public List<AsignaturaDTO> getAll() {
		List<AsignaturaDTO> dtoList = new ArrayList<>();
		asigRepo.findAllConPlanes().forEach((entity) -> dtoList.add(toDTO(entity)));
		dtoList.sort(Comparator.comparing(AsignaturaDTO::getNombre, String.CASE_INSENSITIVE_ORDER));
		return dtoList;
	}

	/**
	 * Convierte los planes de una asignatura a DTO, ordenados por carrera y luego por semestre.
	 * Es estático para que {@code MateriaService} lo use al armar el DTO de un grupo.
	 *
	 * @param entity asignatura de la que se toman los planes
	 * @return lista de {@link PlanEstudioDTO}
	 */
	public static List<PlanEstudioDTO> planesDTO(Asignatura entity) {
		List<PlanEstudioDTO> planes = new ArrayList<>();
		for (PlanEstudio plan : entity.getPlanes()) {
			planes.add(new PlanEstudioDTO(plan.getCarrera(), plan.getSemestre()));
		}
		planes.sort(Comparator.comparing((PlanEstudioDTO p) -> p.getCarrera().name()).thenComparing(PlanEstudioDTO::getSemestre));
		return planes;
	}

	/**
	 * Convierte una asignatura a DTO.
	 *
	 * @param entity asignatura a convertir
	 * @return el DTO correspondiente
	 */
	private AsignaturaDTO toDTO(Asignatura entity) {
		return new AsignaturaDTO(entity.getId(), entity.getNombre(), planesDTO(entity));
	}
}
