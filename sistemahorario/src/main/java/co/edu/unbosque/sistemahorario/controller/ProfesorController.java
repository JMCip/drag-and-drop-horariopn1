package co.edu.unbosque.sistemahorario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.sistemahorario.dto.ProfesorDTO;
import co.edu.unbosque.sistemahorario.service.ProfesorService;

/**
 * Controlador REST de profesores ({@code /profesor}).
 * <p>
 * Listar profesores lo puede hacer cualquier usuario autenticado; crear, actualizar y
 * eliminar es exclusivo de ADMIN (ver {@code SecurityConfig}).
 */
@RestController
@RequestMapping("/profesor")
public class ProfesorController {
	
	@Autowired
	private ProfesorService profServ;
	
	public ProfesorController()
	{
		
	}
	

	/**
	 * Crea un profesor.
	 *
	 * @param nombre      nombre completo
	 * @param contrasenia contraseña en texto plano; se guarda cifrada con BCrypt
	 * @param correo      correo institucional (único en todo el sistema)
	 * @return 201 si se creó; 406 si el correo o la contraseña tienen formato inválido;
	 *         400 si el nombre es inválido o el correo ya existe; 500 en cualquier otro caso
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearProfesor(@RequestParam String nombre, @RequestParam String contrasenia, @RequestParam String correo) {
		
		ProfesorDTO nuevo = new ProfesorDTO(correo, contrasenia, nombre);
		int status = profServ.create(nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Profesor creado con exito", HttpStatus.CREATED);
		case 1:
			return new ResponseEntity<String>("El formato del correo no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 2:
			return new ResponseEntity<String>("El formato de la contraseña no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 3:
			return new ResponseEntity<String>("El nombre ingresado no es valido", HttpStatus.BAD_REQUEST);
		case 4:
			return new ResponseEntity<String>("Correo ya existente", HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	/**
	 * Lista todos los profesores con los ids de las materias que dictan.
	 *
	 * @return 200 con la lista de {@link ProfesorDTO}
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<ProfesorDTO>> obtenertodo(){
		List<ProfesorDTO> listaProfesor = profServ.getAll();
		
		return new ResponseEntity<List<ProfesorDTO>>(listaProfesor, HttpStatus.OK);
	}
	
	
	/**
	 * Elimina un profesor. Sus materias se conservan y quedan sin profesor asignado.
	 *
	 * @param id identificador del profesor
	 * @return 200 si se eliminó; 404 si no existe; 500 en cualquier otro caso
	 */
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarProfesor(@RequestParam Long id){
		int status = profServ.deleteById(id);
		if(status==0) {
			return new ResponseEntity<String>("Profesor eliminado con exito", HttpStatus.OK);
		} else if (status ==1) {
			return new ResponseEntity<String>("No se encontro el profesor", HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	/**
	 * Actualiza los datos de un profesor.
	 *
	 * @param nombre      nuevo nombre
	 * @param contrasenia nueva contraseña; si no se envía se conserva la actual
	 * @param correo      nuevo correo
	 * @param id          identificador del profesor a modificar
	 * @return 200 si se actualizó; 406 si el formato del correo o la contraseña es inválido;
	 *         400 si el nombre es inválido o el correo ya está en uso; 404 si no existe;
	 *         500 en cualquier otro caso
	 */
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarProfesor(@RequestParam String nombre, @RequestParam(required = false) String contrasenia, @RequestParam String correo, @RequestParam Long id) {
		
		ProfesorDTO nuevo = new ProfesorDTO(correo, contrasenia, nombre);
		int status = profServ.updateById(id, nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Profesor actualizado con exito", HttpStatus.OK);
		case 1:
			return new ResponseEntity<String>("El formato del correo no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 2:
			return new ResponseEntity<String>("El formato de la contraseña no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 3:
			return new ResponseEntity<String>("El nombre ingresado no es valido", HttpStatus.BAD_REQUEST);
		case 4:
			return new ResponseEntity<String>("Correo ya en uso", HttpStatus.BAD_REQUEST);
		case 5:
			return new ResponseEntity<String>("Correo ya existente", HttpStatus.BAD_REQUEST);
		case 6:
			return new ResponseEntity<String>("Profesor no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Valida un correo y una contraseña de profesor sin generar token.
	 * <p>
	 * Es un endpoint heredado: el inicio de sesión real es {@code POST /api/auth/login}.
	 * Los parámetros {@code nombre} e {@code id} no se usan en la validación.
	 *
	 * @param nombre      no se usa
	 * @param contrasenia contraseña en texto plano
	 * @param correo      correo del profesor
	 * @param id          no se usa
	 * @return 200 si son correctos; 400 si el formato del correo es inválido; 401 si no coinciden
	 */
	@PostMapping("/iniciarsesion")
	public ResponseEntity<String> validarSesion(@RequestParam String nombre, @RequestParam String contrasenia, @RequestParam String correo, @RequestParam Long id){
		int status = profServ.validarInicioSesion(correo, contrasenia);
		
		switch(status) {
		
		case 0:
			return new ResponseEntity<String>("Sesion iniciada", HttpStatus.OK);
		case 1:
			return new ResponseEntity<String>("Formato de correo no valido", HttpStatus.BAD_REQUEST);	
		case 2:
			return new ResponseEntity<String>("Contraseña o correo no son validos", HttpStatus.UNAUTHORIZED);	
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);	
		}
		
	}
	
	
	
}
