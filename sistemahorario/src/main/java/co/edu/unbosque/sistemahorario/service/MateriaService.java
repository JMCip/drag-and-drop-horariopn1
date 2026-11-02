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

@Service
@Transactional
public class MateriaService implements CRUDOPeration<MateriaDTO>{

	@Autowired
	private MateriaRepository materiaRepo;
	
	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	private EstudianteRepository estRepo;
	
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

	@Override
	public List<MateriaDTO> getAll() {
		List<Materia> entityList = (List<Materia>) materiaRepo.findAll();
		List<MateriaDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(toDTO(entity));
		});
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<Materia> find = materiaRepo.findById(id);
		if(find.isPresent()) {
			materiaRepo.delete(find.get());
			return 0;
		}
		return 1;
	}

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

	@Override
	public long count() {
		return materiaRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return materiaRepo.existsById(id) ? true : false;
	}
	
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

	private Materia toEntity(MateriaDTO data) {
		Materia nueva = new Materia(data.getInicio(), data.getFin(), data.getNombreMateria(), data.getCarrera(), data.getSemestre());
		nueva.setGrupo(data.getGrupo());
		return nueva;
	}

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
