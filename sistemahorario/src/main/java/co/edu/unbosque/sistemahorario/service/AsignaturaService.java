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

// El catalogo se carga por SQL (catalogo_materias.sql); aqui solo se consulta
@Service
@Transactional(readOnly = true)
public class AsignaturaService {

	@Autowired
	private AsignaturaRepository asigRepo;

	public List<AsignaturaDTO> getAll() {
		List<AsignaturaDTO> dtoList = new ArrayList<>();
		asigRepo.findAllConPlanes().forEach((entity) -> dtoList.add(toDTO(entity)));
		dtoList.sort(Comparator.comparing(AsignaturaDTO::getNombre, String.CASE_INSENSITIVE_ORDER));
		return dtoList;
	}

	public static List<PlanEstudioDTO> planesDTO(Asignatura entity) {
		List<PlanEstudioDTO> planes = new ArrayList<>();
		for (PlanEstudio plan : entity.getPlanes()) {
			planes.add(new PlanEstudioDTO(plan.getCarrera(), plan.getSemestre()));
		}
		planes.sort(Comparator.comparing((PlanEstudioDTO p) -> p.getCarrera().name()).thenComparing(PlanEstudioDTO::getSemestre));
		return planes;
	}

	private AsignaturaDTO toDTO(Asignatura entity) {
		return new AsignaturaDTO(entity.getId(), entity.getNombre(), planesDTO(entity));
	}
}
