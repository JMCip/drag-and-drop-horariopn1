package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class MateriaService implements CRUDOPeration<MateriaDTO>{

	@Autowired
	private MateriaRepository materiaRepo;
	
	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	private EstudianteRepository estRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	@Override
	public int create(MateriaDTO data) {
		try {
			LanzadorDeExcepcion.verificarTexto(data.getNombreMateria());
		} catch(InvalidTextFormatException e) {
			System.out.println(e.getMessage());
			return 1;
		}
		
		if(materiaRepo.existByNombreMateria(data.getNombreMateria()))
			return 2;
		
		
		if(data.getSemestre() < 1 || data.getSemestre() > 10) 
			return 3;
			
		materiaRepo.save(mapper.map(data, Materia.class));
		return 0;
	}

	@Override
	public List<MateriaDTO> getAll() {
		List<Materia> entityList = (List<Materia>) materiaRepo.findAll();
		List<MateriaDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			MateriaDTO dto = mapper.map(entity, MateriaDTO.class);
			dtoList.add(dto);
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
		
		try {
			LanzadorDeExcepcion.verificarTexto(data.getNombreMateria());
		} catch(InvalidTextFormatException e) {
			System.out.println(e.getMessage());
			return 1;
		}
		
		if(materiaRepo.existByNombreMateria(data.getNombreMateria()))
			return 2;
		
		
		if(data.getSemestre() < 1 || data.getSemestre() > 10) 
			return 3;
		
		if(find.isPresent()) {
			Materia temp = find.get();
		
			if(temp.getListaEstudiantes().size() != 0) 
				return 4;
			
			
			if(temp.getProfesorAsignado() != null) 
				temp.setProfesorAsignado(null);
			
			
			temp.setInicio(data.getInicio());
			temp.setFin(data.getFin());
			temp.setNombreMateria(data.getNombreMateria());
			temp.setCarrera(data.getCarrera());
			temp.setSemestre(data.getSemestre());
			
			materiaRepo.save(temp);
			return 0;
			
		}
		return 5;
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
			
			if(temp1.getListaMateriasInscritas().contains(temp2)) 
				return 1;
			
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
}
