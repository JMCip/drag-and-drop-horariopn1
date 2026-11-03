package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.sistemahorario.dto.AdminDTO;
import co.edu.unbosque.sistemahorario.entity.Admin;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidEmailFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidPasswordException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

/**
 * Lógica de negocio de los administradores.
 * <p>
 * Valida el formato de los datos, garantiza que el correo sea único entre administradores,
 * profesores y estudiantes, y cifra las contraseñas con BCrypt antes de guardarlas.
 */
@Service
public class AdminService implements CRUDOPeration<AdminDTO>{
	
	@Autowired
	private AdminRepository admRepo;
	
	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	
	private EstudianteRepository estRepo;
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private PasswordEncoder encoder;

	/**
	 * Crea un administrador.
	 *
	 * @param data correo y contraseña del nuevo administrador
	 * @return 0 si se creó; 1 si el formato del correo es inválido; 2 si la contraseña es débil;
	 *         3 si el correo ya existe
	 */
	@Override
	public int create(AdminDTO data) {
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
		
		if(profRepo.existsByCorreo(data.getCorreo()) || estRepo.existsByCorreo(data.getCorreo()) || admRepo.existsByCorreo(data.getCorreo()))
			return 3;
		
		Admin nuevo = mapper.map(data, Admin.class);
		nuevo.setContrasenia(encoder.encode(data.getContrasenia()));
		admRepo.save(nuevo);
		return 0;
	}

	/**
	 * Lista todos los administradores. El DTO incluye la contraseña en su forma cifrada (hash BCrypt).
	 *
	 * @return lista de {@link AdminDTO}
	 */
	@Override
	public List<AdminDTO> getAll() {
		List<Admin> entityList = (List<Admin>) admRepo.findAll();
		List<AdminDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			AdminDTO dto = mapper.map(entity, AdminDTO.class);
			dtoList.add(dto);
		});
		
		return dtoList;
	}

	/**
	 * Elimina un administrador.
	 *
	 * @param id identificador del administrador
	 * @return 0 si se eliminó; 1 si no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Admin> find = admRepo.findById(id);
		if(find.isPresent()) {
			admRepo.delete(find.get());
			return 0;
		}
		return 1;
	}

	/**
	 * Actualiza el correo y, si se envía, la contraseña de un administrador.
	 * Si la contraseña llega vacía se conserva la actual.
	 *
	 * @param id   identificador del administrador
	 * @param data nuevos datos
	 * @return 0 si se actualizó; 1 correo inválido; 2 contraseña débil; 3 el correo ya lo usa un
	 *         profesor o un estudiante; 4 no se encontró el administrador; 5 el correo ya lo usa otro administrador
	 */
	@Override
	public int updateById(Long id, AdminDTO data) {
		Optional<Admin> find = admRepo.findById(id);
		
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
		
		if(profRepo.existsByCorreo(data.getCorreo()) || estRepo.existsByCorreo(data.getCorreo()))
			return 3;
		
		if(find.isPresent()) {
			Admin temp = find.get();
			
			if(admRepo.existsByCorreo(data.getCorreo()) && !temp.getCorreo().equalsIgnoreCase(data.getCorreo()))
				return 5;
			
			temp.setCorreo(data.getCorreo());
			if(cambiaContrasenia)
				temp.setContrasenia(encoder.encode(data.getContrasenia()));
			admRepo.save(temp);
			return 0;
		}
		return 4;
	}

	/**
	 * @return cantidad de administradores registrados
	 */
	@Override
	public long count() {
		return admRepo.count();
	}

	/**
	 * @param id identificador a buscar
	 * @return {@code true} si existe un administrador con ese id
	 */
	@Override
	public boolean exist(Long id) {
		return admRepo.existsById(id);
	}
	
	/**
	 * Comprueba un correo y una contraseña contra los administradores guardados.
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

	    List<Admin> entityList = (List<Admin>) admRepo.findAll();
	    for (Admin user : entityList) {
	        if (user.getCorreo().equalsIgnoreCase(correo) && encoder.matches(pw, user.getContrasenia())) {
	            return 0;
	        }
	    }
	    return 2;
	}
}
