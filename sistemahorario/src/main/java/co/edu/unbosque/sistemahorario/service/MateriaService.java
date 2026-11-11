package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.sistemahorario.dto.MateriaDTO;
import co.edu.unbosque.sistemahorario.entity.Asignatura;
import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Materia;
import co.edu.unbosque.sistemahorario.entity.PlanEstudio;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.repository.AsignaturaRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.MateriaRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidTimeSlotException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

/**
 * Lógica de negocio de los grupos de materias y de sus horarios.
 * <p>
 * Cada {@code Materia} es un grupo concreto de una {@code Asignatura} del catálogo. Este servicio valida el
 * horario, evita cruces para estudiantes y profesores, y administra las inscripciones. También filtra qué
 * grupos puede ver cada estudiante según su carrera y su semestre.
 * Es {@code @Transactional}: si algo falla a la mitad de una operación, no queda guardado nada.
 */
@Service
@Transactional
public class MateriaService implements CRUDOPeration<MateriaDTO>{

	@Autowired
	private MateriaRepository materiaRepo;

	@Autowired
	private AsignaturaRepository asigRepo;

	@Autowired
	private ProfesorRepository profRepo;

	@Autowired
	private EstudianteRepository estRepo;
	
	/**
	 * Crea un grupo de una asignatura del catálogo.
	 *
	 * @param data asignatura ({@code idAsignatura}), grupo y horario (arreglos {@code inicio} y {@code fin})
	 * @return 0 si se creó; 1 la asignatura no existe en el catálogo; 2 ya existe ese grupo de la asignatura;
	 *         4 horario inválido; 5 número de grupo menor a 1
	 */
	@Override
	public int create(MateriaDTO data) {
		Optional<Asignatura> asignatura = asigRepo.findById(data.getIdAsignatura());

		if(asignatura.isEmpty())
			return 1;

		if(materiaRepo.existsByAsignaturaIdAndGrupo(data.getIdAsignatura(), data.getGrupo()))
			return 2;

		if(!horarioValido(data.getInicio(), data.getFin()))
			return 4;

		if(data.getGrupo() < 1)
			return 5;

		materiaRepo.save(new Materia(data.getInicio(), data.getFin(), asignatura.get(), data.getGrupo()));
		return 0;
	}

	/**
	 * Lista todas las materias con su profesor asignado y los ids de sus estudiantes.
	 *
	 * @return lista de {@link MateriaDTO}
	 */
	@Override
	public List<MateriaDTO> getAll() {
		List<MateriaDTO> dtoList = new ArrayList<>();
		materiaRepo.findAll().forEach((entity) -> dtoList.add(toDTO(entity)));
		return dtoList;
	}

	/**
	 * Lista solo los grupos que un estudiante puede ver: los de asignaturas que están en el plan de su
	 * carrera, del semestre 1 hasta el suyo. En cada DTO se llenan {@code carrera} y {@code semestre} con los del plan
	 * que le corresponde. Si el estudiante no existe, devuelve una lista vacía.
	 *
	 * @param idEst identificador del estudiante
	 * @return lista de {@link MateriaDTO} filtrada para ese estudiante
	 */
	// Un estudiante solo ve grupos de materias de su carrera hasta su semestre
	public List<MateriaDTO> getAllParaEstudiante(long idEst) {
		List<MateriaDTO> dtoList = new ArrayList<>();
		Optional<Estudiante> est = estRepo.findById(idEst);

		if(est.isEmpty())
			return dtoList;

		for(Materia entity : materiaRepo.findAll()) {
			Optional<PlanEstudio> plan = planDelEstudiante(est.get(), entity);
			if(plan.isPresent()) {
				MateriaDTO dto = toDTO(entity);
				dto.setCarrera(plan.get().getCarrera());
				dto.setSemestre(plan.get().getSemestre());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * Elimina una materia (grupo).
	 *
	 * @param id identificador de la materia
	 * @return 0 si se eliminó; 1 si no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Materia> find = materiaRepo.findById(id);
		if(find.isPresent()) {
			materiaRepo.delete(find.get());
			return 0;
		}
		return 1;
	}

	/**
	 * Actualiza un grupo. No se permite si ya tiene estudiantes inscritos, para no
	 * cambiarles el horario que eligieron. Si cambia el horario, se retira el profesor asignado
	 * porque podría quedar con un cruce.
	 *
	 * @param id   identificador del grupo
	 * @param data nuevos datos
	 * @return 0 si se actualizó; 1 la asignatura no existe en el catálogo; 2 ya existe ese grupo de la asignatura;
	 *         4 el grupo tiene estudiantes inscritos; 5 no se encontró el grupo; 6 horario inválido;
	 *         7 número de grupo menor a 1
	 */
	@Override
	public int updateById(Long id, MateriaDTO data) {
		Optional<Materia> find = materiaRepo.findById(id);

		if(find.isEmpty())
			return 5;

		Materia temp = find.get();
		Optional<Asignatura> asignatura = asigRepo.findById(data.getIdAsignatura());

		if(asignatura.isEmpty())
			return 1;

		boolean cambioAsignaturaOGrupo = temp.getAsignatura().getId() != data.getIdAsignatura() || temp.getGrupo() != data.getGrupo();
		if(cambioAsignaturaOGrupo && materiaRepo.existsByAsignaturaIdAndGrupo(data.getIdAsignatura(), data.getGrupo()))
			return 2;

		if(!horarioValido(data.getInicio(), data.getFin()))
			return 6;

		if(data.getGrupo() < 1)
			return 7;

		if(temp.getListaEstudiantes() != null && !temp.getListaEstudiantes().isEmpty())
			return 4;

		boolean cambioHorario = !Arrays.equals(temp.getInicio(), data.getInicio()) || !Arrays.equals(temp.getFin(), data.getFin());
		if(cambioHorario)
			temp.setProfesorAsignado(null);

		temp.setInicio(data.getInicio());
		temp.setFin(data.getFin());
		temp.setAsignatura(asignatura.get());
		temp.setGrupo(data.getGrupo());

		materiaRepo.save(temp);
		return 0;
	}

	/**
	 * @return cantidad de materias registradas
	 */
	@Override
	public long count() {
		return materiaRepo.count();
	}

	/**
	 * @param id identificador a buscar
	 * @return {@code true} si existe una materia con ese id
	 */
	@Override
	public boolean exist(Long id) {
		return materiaRepo.existsById(id) ? true : false;
	}
	
	/**
	 * Asigna un profesor a un grupo, siempre que no haya cruce de horario. Si el grupo ya tenía
	 * otro profesor, se reemplaza; solo se rechaza si es el mismo.
	 *
	 * @param idProfesor identificador del profesor
	 * @param idMateria  identificador del grupo
	 * @return 0 si se asignó; 1 ese mismo profesor ya está asignado; 2 el horario se cruza con el del profesor;
	 *         3 no se encontró el profesor o el grupo
	 */
	public int asignarProfesor(long idProfesor, long idMateria) {
		Optional<Profesor> prof = profRepo.findById(idProfesor);
		Optional<Materia> materia = materiaRepo.findById(idMateria);

		if(materia.isPresent() && prof.isPresent()) {
			Profesor temp1 = prof.get();
			Materia temp2 = materia.get();

			// Se puede reemplazar al profesor; solo se rechaza si es el mismo
			if(temp2.getProfesorAsignado() != null && temp2.getProfesorAsignado().getId() == temp1.getId())
				return 1;

			try {
				LanzadorDeExcepcion.verificarDisponibilidad(temp1, temp2);
			} catch(InvalidTimeSlotException e) {
				System.out.println(e.getMessage());
				return 2;
			}

			temp2.setProfesorAsignado(temp1);
			materiaRepo.save(temp2);
			return 0;
		}

		return 3;
	}

	/**
	 * Quita el profesor asignado a un grupo.
	 *
	 * @param idMateria identificador del grupo
	 * @return 0 si se quitó; 1 el grupo no tenía profesor; 2 no se encontró el grupo
	 */
	public int quitarProfesor(long idMateria) {
		Optional<Materia> materia = materiaRepo.findById(idMateria);

		if(materia.isEmpty())
			return 2;

		if(materia.get().getProfesorAsignado() == null)
			return 1;

		materia.get().setProfesorAsignado(null);
		materiaRepo.save(materia.get());
		return 0;
	}

	/**
	 * Inscribe un estudiante en un grupo.
	 * <p>
	 * Reglas: la asignatura debe estar en el plan de su carrera, en su semestre o en uno anterior; no puede
	 * estar ya inscrito en ese grupo; no puede cursar dos grupos de la misma asignatura y el horario no puede
	 * cruzarse con el de sus otras materias.
	 *
	 * @param idEst     identificador del estudiante
	 * @param idMateria identificador del grupo
	 * @return 0 si quedó inscrito; 1 ya estaba inscrito; 2 hay cruce de horario; 3 no se encontró
	 *         el estudiante o el grupo; 4 ya está inscrito en otro grupo de la misma asignatura;
	 *         5 la asignatura no corresponde a su carrera o a su semestre
	 */
	public int inscribirEstudiante(long idEst, long idMateria) {
		Optional<Estudiante> est = estRepo.findById(idEst);
		Optional<Materia> materia = materiaRepo.findById(idMateria);

		if(est.isPresent() && materia.isPresent()){
			Estudiante temp1 = est.get();
			Materia temp2 = materia.get();

			if(planDelEstudiante(temp1, temp2).isEmpty())
				return 5;

			if(temp1.getListaMateriasInscritas().stream().anyMatch(m -> m.getId() == temp2.getId()))
				return 1;

			if(temp1.getListaMateriasInscritas().stream().anyMatch(m -> m.getAsignatura().getId() == temp2.getAsignatura().getId()))
				return 4;

			try {
				LanzadorDeExcepcion.verificarDisponibilidad(temp1, temp2);
			} catch(InvalidTimeSlotException e) {
				System.out.println(e.getMessage());
				return 2;
			}

			temp2.getListaEstudiantes().add(temp1);
			materiaRepo.save(temp2);
			return 0;
		}

		return 3;
	}

	/**
	 * Quita a un estudiante de una materia.
	 *
	 * @param idEst     identificador del estudiante
	 * @param idMateria identificador de la materia
	 * @return 0 si se quitó; 1 el estudiante no estaba inscrito; 2 no se encontró el estudiante o la materia
	 */
	public int desinscribirEstudiante(long idEst, long idMateria) {
		Optional<Estudiante> est = estRepo.findById(idEst);
		Optional<Materia> materia = materiaRepo.findById(idMateria);

		if(est.isPresent() && materia.isPresent()) {
			Materia temp = materia.get();

			if(!temp.getListaEstudiantes().removeIf(e -> e.getId() == idEst))
				return 1;

			materiaRepo.save(temp);
			return 0;
		}

		return 2;
	}

	/**
	 * Mueve a un estudiante de un grupo a otro de la misma asignatura (reubicación).
	 * <p>
	 * Se valida el cruce del grupo destino contra las demás materias del estudiante, sin contar
	 * el grupo de origen, porque ese lo va a dejar libre.
	 *
	 * @param idEst     identificador del estudiante
	 * @param idOrigen  grupo del que sale
	 * @param idDestino grupo al que entra
	 * @return 0 si se movió; 1 no estaba inscrito en el origen; 2 los grupos no son de la misma asignatura;
	 *         3 ya está en el destino; 4 el destino se cruza con otra materia suya; 5 no se encontró
	 *         el estudiante o alguno de los grupos
	 */
	public int moverEstudiante(long idEst, long idOrigen, long idDestino) {
		Optional<Estudiante> est = estRepo.findById(idEst);
		Optional<Materia> origen = materiaRepo.findById(idOrigen);
		Optional<Materia> destino = materiaRepo.findById(idDestino);

		if(est.isEmpty() || origen.isEmpty() || destino.isEmpty())
			return 5;

		Materia temp1 = origen.get();
		Materia temp2 = destino.get();

		if(temp1.getListaEstudiantes().stream().noneMatch(e -> e.getId() == idEst))
			return 1;

		if(temp1.getAsignatura().getId() != temp2.getAsignatura().getId())
			return 2;

		if(temp2.getListaEstudiantes().stream().anyMatch(e -> e.getId() == idEst))
			return 3;

		for(Materia otra : est.get().getListaMateriasInscritas()) {
			if(otra.getId() != idOrigen && LanzadorDeExcepcion.hayCruce(otra, temp2))
				return 4;
		}

		temp1.getListaEstudiantes().removeIf(e -> e.getId() == idEst);
		temp2.getListaEstudiantes().add(est.get());
		materiaRepo.save(temp1);
		materiaRepo.save(temp2);
		return 0;
	}

	/**
	 * Busca el plan de estudios que le corresponde al estudiante para ese grupo: el de su carrera en el que la
	 * asignatura va en su semestre o antes. Si hay varios, toma el de menor semestre.
	 *
	 * @param est     estudiante a revisar
	 * @param materia grupo a revisar
	 * @return el plan, o vacío si el estudiante no tiene carrera o semestre, o la asignatura no corresponde a su plan
	 */
	// Plan de la carrera del estudiante en el que la materia va en su semestre o antes
	private Optional<PlanEstudio> planDelEstudiante(Estudiante est, Materia materia) {
		if(est.getCarrera() == null || est.getSemestre() == null)
			return Optional.empty();

		return materia.getAsignatura().getPlanes().stream()
				.filter(p -> p.getCarrera() == est.getCarrera() && p.getSemestre() <= est.getSemestre())
				.min(Comparator.comparing(PlanEstudio::getSemestre));
	}

	/**
	 * Verifica que un horario sea utilizable.
	 * <p>
	 * Debe tener exactamente 5 valores (lunes a viernes), al menos un día con clase (no todos en
	 * {@code -1}) y cumplir las franjas de {@code LanzadorDeExcepcion.verificarFranjaHoraria}.
	 *
	 * @param inicio hora de inicio por día
	 * @param fin    hora de fin por día
	 * @return {@code true} si el horario es válido
	 */
	private boolean horarioValido(int[] inicio, int[] fin) {
		if(inicio == null || fin == null || inicio.length != 5 || fin.length != 5)
			return false;

		if(Arrays.stream(inicio).allMatch(h -> h == -1))
			return false;

		try {
			LanzadorDeExcepcion.verificarFranjaHoraria(inicio, fin);
		} catch(InvalidTimeSlotException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}



	/**
	 * Convierte un grupo a DTO. Incluye el nombre y los planes de la asignatura; el profesor y
	 * los estudiantes se reducen a sus ids.
	 *
	 * @param entity grupo a convertir
	 * @return el DTO correspondiente
	 */
	private MateriaDTO toDTO(Materia entity) {
		MateriaDTO dto = new MateriaDTO(entity.getInicio(), entity.getFin(), entity.getAsignatura().getId(), entity.getGrupo());
		dto.setId(entity.getId());
		dto.setNombreMateria(entity.getAsignatura().getNombre());
		dto.setPlanes(AsignaturaService.planesDTO(entity.getAsignatura()));
		dto.setIdProfesorAsignado(entity.getProfesorAsignado() != null ? entity.getProfesorAsignado().getId() : null);

		List<Long> idsEstudiantes = new ArrayList<>();
		if(entity.getListaEstudiantes() != null)
			entity.getListaEstudiantes().forEach((est) -> idsEstudiantes.add(est.getId()));
		dto.setListaIdEstudiantes(idsEstudiantes);
		return dto;
	}
}
