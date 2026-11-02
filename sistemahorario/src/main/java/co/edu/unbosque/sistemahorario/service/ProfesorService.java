package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.sistemahorario.dto.ProfesorDTO;
import co.edu.unbosque.sistemahorario.entity.Materia;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidEmailFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidNameFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidPasswordException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

@Service
@Transactional
public class ProfesorService implements CRUDOPeration<ProfesorDTO>{

	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	private EstudianteRepository estRepo;
	
	@Autowired
	private AdminRepository admRepo;
	
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public int create(ProfesorDTO data) {
		
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
		
		profRepo.save(new Profesor(data.getCorreo(), encoder.encode(data.getContrasenia()), data.getNombre()));
		return 0;
	}

	@Override
	public List<ProfesorDTO> getAll() {
		List<Profesor> entityList = (List<Profesor>) profRepo.findAll();
		List<ProfesorDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(toDTO(entity));
		});
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<Profesor> find = profRepo.findById(id);
		
		if(find.isPresent()) {
			// Sus materias se conservan, quedan sin profesor asignado
			for (Materia materia : find.get().getListaMaterias()) {
				materia.setProfesorAsignado(null);
			}
			profRepo.delete(find.get());
			return 0;
		}
		
		return 1;
	}

	@Override
	public int updateById(Long id, ProfesorDTO data) {
		Optional<Profesor> find = profRepo.findById(id);

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
		
		if(estRepo.existsByCorreo(data.getCorreo()) || admRepo.existsByCorreo(data.getCorreo()))
			return 4;
		
		if(find.isPresent()) {
			Profesor temp = find.get();
			
			if(profRepo.existsByCorreo(data.getCorreo()) && !temp.getCorreo().equalsIgnoreCase(data.getCorreo()))
				return 5;
			
			temp.setNombre(data.getNombre());
			temp.setCorreo(data.getCorreo());
			if(cambiaContrasenia)
				temp.setContrasenia(encoder.encode(data.getContrasenia()));
			profRepo.save(temp);
			return 0;
		}
		
		return 6;
	}
	
	private ProfesorDTO toDTO(Profesor entity) {
		List<Long> idsMaterias = new ArrayList<>();
		entity.getListaMaterias().forEach((materia) -> idsMaterias.add(materia.getId()));
		
		ProfesorDTO dto = new ProfesorDTO(entity.getCorreo(), null, idsMaterias, entity.getNombre());
		dto.setId(entity.getId());
		return dto;
	}
	
	public int validarInicioSesion(String correo, String pw) {
	    try {
	        LanzadorDeExcepcion.verificarEmail(correo);
	    } catch (InvalidEmailFormatException e) {
	        System.out.println("Error de correo: " + e.getMessage());
	        return 1;
	    }

	    List<Profesor> entityList = (List<Profesor>) profRepo.findAll();
	    for (Profesor user : entityList) {
	        if (user.getCorreo().equalsIgnoreCase(correo) && encoder.matches(pw, user.getContrasenia())) {
	            return 0;
	        }
	    }
	    return 2;
	}

	@Override
	public long count() {
		return profRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return profRepo.existsById(id) ? true : false;
	}
}
