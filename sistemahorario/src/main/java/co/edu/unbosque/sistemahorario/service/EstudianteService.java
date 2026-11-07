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

/**
 * Lógica de negocio de los estudiantes.
 * <p>
 * Valida correo, contraseña y nombre; garantiza que el correo sea único en todo el sistema
 * y cifra la contraseña con BCrypt. Es {@code @Transactional} para que los cambios sobre las
 * relaciones con las materias se guarden de forma consistente.
 */
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

	/**
	 * Crea un estudiante.
	 *
	 * @param data correo, contraseña, nombre y carrera
	 * @return 0 si se creó; 1 correo inválido; 2 contraseña débil; 3 nombre inválido; 4 correo ya existente
	 */
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

	/**
	 * Lista todos los estudiantes, con los ids de sus materias inscritas.
	 *
	 * @return lista de {@link EstudianteDTO}
	 */
	@Override
	public List<EstudianteDTO> getAll() {
		List<Estudiante> entityList = (List<Estudiante>) estRepo.findAll();
		List<EstudianteDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(toDTO(entity));
		});
		
		return dtoList;
	}

	/**
	 * Elimina un estudiante. Como {@code Materia} es la dueña de la relación, primero se
	 * le saca de todas sus materias y luego se borra.
	 *
	 * @param id identificador del estudiante
	 * @return 0 si se eliminó; 1 si no existe
	 */
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

	/**
	 * Actualiza nombre, carrera, correo y, si se envía, la contraseña de un estudiante.
	 * Si la contraseña llega vacía se conserva la actual.
	 *
	 * @param id   identificador del estudiante
	 * @param data nuevos datos
	 * @return 0 si se actualizó; 1 correo inválido; 2 contraseña débil; 3 nombre inválido;
	 *         4 el correo ya lo usa un profesor o administrador; 5 el correo ya lo usa otro estudiante;
	 *         6 no se encontró el estudiante
	 */
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

	/**
	 * @return cantidad de estudiantes registrados
	 */
	@Override
	public long count() {
		return estRepo.count();
	}

	/**
	 * @param id identificador a buscar
	 * @return {@code true} si existe un estudiante con ese id
	 */
	@Override
	public boolean exist(Long id) {
		return estRepo.existsById(id);
	}
	
	/**
	 * Convierte un estudiante a DTO. La contraseña no se incluye y las materias se
	 * reducen a sus ids.
	 *
	 * @param entity estudiante a convertir
	 * @return el DTO correspondiente
	 */
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
	
	/**
	 * Comprueba un correo y una contraseña contra los estudiantes guardados.
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

	    List<Estudiante> entityList = (List<Estudiante>) estRepo.findAll();
	    for (Estudiante user : entityList) {
	        if (user.getCorreo().equalsIgnoreCase(correo) && encoder.matches(pw, user.getContrasenia())) {
	            return 0;
	        }
	    }
	    return 2;
	}
}
