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

/**
 * Lógica de negocio de los profesores.
 * <p>
 * Valida correo, contraseña y nombre; garantiza que el correo sea único en todo el sistema
 * y cifra la contraseña con BCrypt.
 */
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

	/**
	 * Crea un profesor.
	 *
	 * @param data correo, contraseña y nombre
	 * @return 0 si se creó; 1 correo inválido; 2 contraseña débil; 3 nombre inválido; 4 correo ya existente
	 */
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

	/**
	 * Lista todos los profesores, con los ids de las materias que dictan.
	 *
	 * @return lista de {@link ProfesorDTO}
	 */
	@Override
	public List<ProfesorDTO> getAll() {
		List<Profesor> entityList = (List<Profesor>) profRepo.findAll();
		List<ProfesorDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(toDTO(entity));
		});
		return dtoList;
	}

	/**
	 * Elimina un profesor. Sus materias se conservan y quedan sin profesor asignado.
	 *
	 * @param id identificador del profesor
	 * @return 0 si se eliminó; 1 si no existe
	 */
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

	/**
	 * Actualiza nombre, correo y, si se envía, la contraseña de un profesor.
	 * Si la contraseña llega vacía se conserva la actual.
	 *
	 * @param id   identificador del profesor
	 * @param data nuevos datos
	 * @return 0 si se actualizó; 1 correo inválido; 2 contraseña débil; 3 nombre inválido;
	 *         4 el correo ya lo usa un estudiante o administrador; 5 el correo ya lo usa otro profesor;
	 *         6 no se encontró el profesor
	 */
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
	
	/**
	 * Convierte un profesor a DTO. La contraseña no se incluye y las materias se
	 * reducen a sus ids.
	 *
	 * @param entity profesor a convertir
	 * @return el DTO correspondiente
	 */
	private ProfesorDTO toDTO(Profesor entity) {
		List<Long> idsMaterias = new ArrayList<>();
		entity.getListaMaterias().forEach((materia) -> idsMaterias.add(materia.getId()));
		
		ProfesorDTO dto = new ProfesorDTO(entity.getCorreo(), null, idsMaterias, entity.getNombre());
		dto.setId(entity.getId());
		return dto;
	}
	
	/**
	 * Comprueba un correo y una contraseña contra los profesores guardados.
	 * Es la versión heredada del login; el flujo actual usa JWT ({@code AuthController}).
	 *
	 * @param correo correo ingresado
	 * @param pw     contraseña en texto plano (se compara contra el hash BCrypt)
	 * @return 0 si son correctos; 1 si el formato del correo es inválido; 2 si no coinciden
	 */
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

	/**
	 * @return cantidad de profesores registrados
	 */
	@Override
	public long count() {
		return profRepo.count();
	}

	/**
	 * @param id identificador a buscar
	 * @return {@code true} si existe un profesor con ese id
	 */
	@Override
	public boolean exist(Long id) {
		return profRepo.existsById(id) ? true : false;
	}
}
