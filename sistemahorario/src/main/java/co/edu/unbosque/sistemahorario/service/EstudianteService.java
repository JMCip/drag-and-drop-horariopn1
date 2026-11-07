package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.sistemahorario.dto.EstudianteDTO;
import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Materia;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidEmailFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidNameFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidPasswordException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

@Service
@Transactional
public class EstudianteService implements CRUDOPeration<EstudianteDTO>{

	@Autowired
	private EstudianteRepository estRepo;

	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	private AdminRepository admRepo;
	
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public int create(EstudianteDTO data) {
		try {
			LanzadorDeExcepcion.verificarEmail(data.getCorreo());
		} catch(InvalidEmailFormatException e) {
			System.out.println("Error de correo: " + e.getMessage());
			return 1;
		}
		
		try {
			LanzadorDeExcepcion.verificarContrasenia(data.getContrasenia());
		} catch(InvalidPasswordException e) {
			System.out.println("Error de contraseña: " + e.getMessage());
			return 2;
		}
		
		try {
			LanzadorDeExcepcion.verificarNombre(data.getNombre());
		} catch(InvalidNameFormatException e) {
			System.out.println("Error de nombre: " + e.getMessage());
			return 3;
		}
		
		if(profRepo.existsByCorreo(data.getCorreo()) || estRepo.existsByCorreo(data.getCorreo()) || admRepo.existsByCorreo(data.getCorreo()))
			return 4;
		
		if(!semestreValido(data.getSemestre()))
			return 5;
		
		Estudiante nuevo = new Estudiante(data.getCorreo(), encoder.encode(data.getContrasenia()), data.getNombre());
		nuevo.setCarrera(data.getCarrera());
		nuevo.setSemestre(data.getSemestre());
		estRepo.save(nuevo);
		return 0;
	}

	@Override
	public List<EstudianteDTO> getAll() {
		List<Estudiante> entityList = (List<Estudiante>) estRepo.findAll();
		List<EstudianteDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(toDTO(entity));
		});
		
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<Estudiante> find = estRepo.findById(id);
		if(find.isPresent()) {
			// Materia es la duena de la relacion: hay que sacarlo de sus materias antes de borrarlo
			for (Materia materia : find.get().getListaMateriasInscritas()) {
				materia.getListaEstudiantes().removeIf(e -> e.getId() == id);
			}
			estRepo.delete(find.get());
			return 0;
		}
		return 1;
	}

	@Override
	public int updateById(Long id, EstudianteDTO data) {
		Optional<Estudiante> find = estRepo.findById(id);
		
		try {
			LanzadorDeExcepcion.verificarEmail(data.getCorreo());
		} catch(InvalidEmailFormatException e) {
			System.out.println("Error de correo: " + e.getMessage());
			return 1;
		}
		
		// Sin contraseña se conserva la actual
		boolean cambiaContrasenia = data.getContrasenia() != null && !data.getContrasenia().isBlank();

		if(cambiaContrasenia) {
			try {
				LanzadorDeExcepcion.verificarContrasenia(data.getContrasenia());
			} catch(InvalidPasswordException e) {
				System.out.println("Error de contraseña: " + e.getMessage());
				return 2;
			}
		}
		
		try {
			LanzadorDeExcepcion.verificarNombre(data.getNombre());
		} catch(InvalidNameFormatException e) {
			System.out.println("Error de nombre: " + e.getMessage());
			return 3;
		}
		
		if(profRepo.existsByCorreo(data.getCorreo()) || admRepo.existsByCorreo(data.getCorreo()))
			return 4;
		
		if(!semestreValido(data.getSemestre()))
			return 7;
		
		if(find.isPresent()) {
			Estudiante temp = find.get();
			
			if(estRepo.existsByCorreo(data.getCorreo()) && !temp.getCorreo().equalsIgnoreCase(data.getCorreo()))
				return 5;
			
			temp.setNombre(data.getNombre());
			temp.setCarrera(data.getCarrera());
			temp.setSemestre(data.getSemestre());
			temp.setCorreo(data.getCorreo());
			if(cambiaContrasenia)
				temp.setContrasenia(encoder.encode(data.getContrasenia()));
			estRepo.save(temp);
			return 0;
		}
		
		return 6;
	}

	@Override
	public long count() {
		return estRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return estRepo.existsById(id);
	}
	
	private EstudianteDTO toDTO(Estudiante entity) {
		List<Long> idsMaterias = new ArrayList<>();
		entity.getListaMateriasInscritas().forEach((materia) -> idsMaterias.add(materia.getId()));
		
		EstudianteDTO dto = new EstudianteDTO(entity.getCorreo(), null, idsMaterias, entity.getNombre());
		dto.setId(entity.getId());
		dto.setCarrera(entity.getCarrera());
		dto.setSemestre(entity.getSemestre());
		return dto;
	}
	
	private boolean semestreValido(Short semestre) {
		return semestre != null && semestre >= 1 && semestre <= 12;
	}
	
	public int validarInicioSesion(String correo, String pw) {
	    try {
	        LanzadorDeExcepcion.verificarEmail(correo);
	    } catch (InvalidEmailFormatException e) {
	        System.out.println("Error de correo: " + e.getMessage());
	        return 1;
	    }

	    List<Estudiante> entityList = (List<Estudiante>) estRepo.findAll();
	    for (Estudiante user : entityList) {
	        if (user.getCorreo().equalsIgnoreCase(correo) && encoder.matches(pw, user.getContrasenia())) {
	            return 0;
	        }
	    }
	    return 2;
	}
}
