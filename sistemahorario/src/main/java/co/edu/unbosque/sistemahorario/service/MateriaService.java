package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.sistemahorario.dto.MateriaDTO;
import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Materia;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.MateriaRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidTextFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidTimeSlotException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

/**
 * Lógica de negocio de las materias y de sus horarios.
 * <p>
 * Cada {@code Materia} es un grupo concreto de una asignatura. Este servicio valida el
 * horario, evita cruces para estudiantes y profesores, y administra las inscripciones.
 * Es {@code @Transactional}: si algo falla a la mitad de una operación, no queda guardado nada.
 */
@Service
@Transactional
public class MateriaService implements CRUDOPeration<MateriaDTO>{

	@Autowired
	private MateriaRepository materiaRepo;
	
	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	private EstudianteRepository estRepo;
	
	/**
	 * Crea un grupo de una materia.
	 *
	 * @param data nombre, carrera, semestre, grupo y horario (arreglos {@code inicio} y {@code fin})
	 * @return 0 si se creó; 1 nombre con formato inválido; 2 ya existe ese grupo de la materia;
	 *         3 semestre fuera del rango 1 a 10; 4 horario inválido; 5 número de grupo menor a 1
	 */
	@Override
	public int create(MateriaDTO data) {
		try {
			LanzadorDeExcepcion.verificarTexto(data.getNombreMateria());
		} catch(InvalidTextFormatException e) {
			System.out.println(e.getMessage());
			return 1;
		}
		
		if(materiaRepo.existsByNombreMateriaAndGrupo(data.getNombreMateria(), data.getGrupo()))
			return 2;


		if(data.getSemestre() < 1 || data.getSemestre() > 10)
			return 3;

		if(!horarioValido(data.getInicio(), data.getFin()))
			return 4;

		if(data.getGrupo() < 1)
			return 5;

		materiaRepo.save(toEntity(data));
		return 0;
	}

	/**
	 * Lista todas las materias con su profesor asignado y los ids de sus estudiantes.
	 *
	 * @return lista de {@link MateriaDTO}
	 */
	@Override
	public List<MateriaDTO> getAll() {
		List<Materia> entityList = (List<Materia>) materiaRepo.findAll();
		List<MateriaDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(toDTO(entity));
		});
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
	 * Actualiza una materia. No se permite si ya tiene estudiantes inscritos, para no
	 * cambiarles el horario que eligieron. Si cambia el horario, se retira el profesor asignado
	 * porque podría quedar con un cruce.
	 *
	 * @param id   identificador de la materia
	 * @param data nuevos datos
	 * @return 0 si se actualizó; 1 nombre inválido; 2 ya existe ese grupo; 3 semestre fuera de 1 a 10;
	 *         4 la materia tiene estudiantes inscritos; 5 no se encontró la materia; 6 horario inválido;
	 *         7 número de grupo menor a 1
	 */
	@Override
	public int updateById(Long id, MateriaDTO data) {
		Optional<Materia> find = materiaRepo.findById(id);

		if(find.isEmpty())
			return 5;

		Materia temp = find.get();

		try {
			LanzadorDeExcepcion.verificarTexto(data.getNombreMateria());
		} catch(InvalidTextFormatException e) {
			System.out.println(e.getMessage());
			return 1;
		}

		boolean cambioNombreOGrupo = !temp.getNombreMateria().equalsIgnoreCase(data.getNombreMateria()) || temp.getGrupo() != data.getGrupo();
		if(cambioNombreOGrupo && materiaRepo.existsByNombreMateriaAndGrupo(data.getNombreMateria(), data.getGrupo()))
			return 2;


		if(data.getSemestre() < 1 || data.getSemestre() > 10)
			return 3;

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
		temp.setNombreMateria(data.getNombreMateria());
		temp.setGrupo(data.getGrupo());
		temp.setCarrera(data.getCarrera());
		temp.setSemestre(data.getSemestre());

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
	 * Asigna un profesor a una materia, si la materia no tiene profesor y no hay cruce de horario.
	 *
	 * @param idProfesor identificador del profesor
	 * @param idMateria  identificador de la materia
	 * @return 0 si se asignó; 1 la materia ya tiene profesor; 2 el horario se cruza con el del profesor;
	 *         3 no se encontró el profesor o la materia
	 */
	public int asignarProfesor(long idProfesor, long idMateria) {
		Optional<Profesor> prof = profRepo.findById(idProfesor);
		Optional<Materia> materia = materiaRepo.findById(idMateria);
		
		if(materia.isPresent() && prof.isPresent()) {
			Profesor temp1 = prof.get();
			Materia temp2 = materia.get();
			
			if(temp2.getProfesorAsignado() != null) 
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
	 * Inscribe un estudiante en un grupo de una materia.
	 * <p>
	 * Reglas: no puede estar ya inscrito en ese grupo, no puede cursar dos grupos de la misma
	 * materia y el horario no puede cruzarse con el de sus otras materias.
	 *
	 * @param idEst     identificador del estudiante
	 * @param idMateria identificador de la materia (grupo)
	 * @return 0 si quedó inscrito; 1 ya estaba inscrito; 2 hay cruce de horario; 3 no se encontró
	 *         el estudiante o la materia; 4 ya está inscrito en otro grupo de la misma materia
	 */
	public int inscribirEstudiante(long idEst, long idMateria) {
		Optional<Estudiante> est = estRepo.findById(idEst);
		Optional<Materia> materia = materiaRepo.findById(idMateria);
		
		if(est.isPresent() && materia.isPresent()){
			Estudiante temp1 = est.get();
			Materia temp2 = materia.get();
			
			if(temp1.getListaMateriasInscritas().stream().anyMatch(m -> m.getId() == temp2.getId()))
				return 1;

			if(temp1.getListaMateriasInscritas().stream().anyMatch(m -> m.getNombreMateria().equalsIgnoreCase(temp2.getNombreMateria())))
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
	 * Mueve a un estudiante de un grupo a otro de la misma materia (reubicación).
	 * <p>
	 * Se valida el cruce del grupo destino contra las demás materias del estudiante, sin contar
	 * el grupo de origen, porque ese lo va a dejar libre.
	 *
	 * @param idEst     identificador del estudiante
	 * @param idOrigen  grupo del que sale
	 * @param idDestino grupo al que entra
	 * @return 0 si se movió; 1 no estaba inscrito en el origen; 2 los grupos no son de la misma materia;
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

		if(!temp1.getNombreMateria().equalsIgnoreCase(temp2.getNombreMateria()))
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
	 * Convierte un DTO a entidad para guardarlo.
	 *
	 * @param data DTO con los datos de la materia
	 * @return la entidad nueva (sin id, sin profesor y sin estudiantes)
	 */
	private Materia toEntity(MateriaDTO data) {
		Materia nueva = new Materia(data.getInicio(), data.getFin(), data.getNombreMateria(), data.getCarrera(), data.getSemestre());
		nueva.setGrupo(data.getGrupo());
		return nueva;
	}

	/**
	 * Convierte una materia a DTO. El profesor y los estudiantes se reducen a sus ids.
	 *
	 * @param entity materia a convertir
	 * @return el DTO correspondiente
	 */
	private MateriaDTO toDTO(Materia entity) {
		Long idProfesor = entity.getProfesorAsignado() != null ? entity.getProfesorAsignado().getId() : null;

		List<Long> idsEstudiantes = new ArrayList<>();
		if(entity.getListaEstudiantes() != null)
			entity.getListaEstudiantes().forEach((est) -> idsEstudiantes.add(est.getId()));

		MateriaDTO dto = new MateriaDTO(entity.getInicio(), entity.getFin(), entity.getNombreMateria(), entity.getCarrera(),
				entity.getSemestre(), idProfesor, idsEstudiantes);
		dto.setId(entity.getId());
		dto.setGrupo(entity.getGrupo());
		return dto;
	}
}
