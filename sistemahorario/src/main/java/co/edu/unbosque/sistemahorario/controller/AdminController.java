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


import co.edu.unbosque.sistemahorario.dto.AdminDTO;
import co.edu.unbosque.sistemahorario.service.AdminService;



/**
 * Controlador REST de administradores ({@code /admin}).
 * <p>
 * Según {@code SecurityConfig}, solo el rol ADMIN puede usar estos endpoints.
 * Los servicios devuelven un código entero (0 = éxito) y este controlador lo traduce
 * a un estado HTTP con un mensaje.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
	
	
	@Autowired
	private AdminService admServ;
	
	public AdminController() {
		
	}
	
	/**
	 * Crea un administrador.
	 *
	 * @param correo      correo del nuevo administrador (debe ser único en todo el sistema)
	 * @param contrasenia contraseña en texto plano; se guarda cifrada con BCrypt
	 * @return 201 si se creó; 406 si el correo o la contraseña tienen formato inválido;
	 *         400 si el correo ya existe; 500 en cualquier otro caso
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crearAdmin(@RequestParam String correo, @RequestParam String contrasenia) {
		
		AdminDTO nuevo = new AdminDTO(correo, contrasenia);
		int status = admServ.create(nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Administrador creado con exito", HttpStatus.CREATED);
		case 1:
			return new ResponseEntity<String>("El formato del correo no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 2:
			return new ResponseEntity<String>("El formato de la contraseña no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 3:
			return new ResponseEntity<String>("Correo ya existente en el sistema", HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		
		}
					
}
	
	/**
	 * Lista todos los administradores.
	 *
	 * @return 200 con la lista de {@link AdminDTO}
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<AdminDTO>> obtenertodo() {
		List<AdminDTO> listaAdmin = admServ.getAll();
		
		return new ResponseEntity<List<AdminDTO>>(listaAdmin, HttpStatus.OK);
		
		}
	
	/**
	 * Elimina un administrador por su id.
	 *
	 * @param id identificador del administrador
	 * @return 200 si se eliminó; 404 si no existe; 500 en cualquier otro caso
	 */
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarAdmin(@RequestParam Long id) {
	
		int status = admServ.deleteById(id);
		if(status==0) {
			return new ResponseEntity<String>("Admin eliminado correctamente!",HttpStatus.OK);
		}
		else if(status==1) {
			return new ResponseEntity<String>("No se encontro el Admin",HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<String>("Error interno del servidor",HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Actualiza el correo (y opcionalmente la contraseña) de un administrador.
	 *
	 * @param correo      nuevo correo
	 * @param contrasenia nueva contraseña; si no se envía se conserva la actual
	 * @param id          identificador del administrador a modificar
	 * @return 200 si se actualizó; 406 si el formato del correo o la contraseña es inválido;
	 *         400 si el correo ya está en uso; 404 si no existe; 500 en cualquier otro caso
	 */
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarAdmin(@RequestParam String correo, @RequestParam(required = false) String contrasenia, @RequestParam Long id) {
		
		AdminDTO nuevo = new AdminDTO(correo, contrasenia);
		int status = admServ.updateById(id,nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Administrador actualizado con exito", HttpStatus.OK);
		case 1:
			return new ResponseEntity<String>("El formato del correo no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 2:
			return new ResponseEntity<String>("El formato de la contraseña no es valido", HttpStatus.NOT_ACCEPTABLE);
		case 3:
			return new ResponseEntity<String>("Correo ya existente en el sistema", HttpStatus.BAD_REQUEST);
		case 4:
			return new ResponseEntity<String>("Admin no encontrado", HttpStatus.NOT_FOUND);
		case 5:
			return new ResponseEntity<String>("Correo ya existente en el sistema", HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		
		}
		
	}
	
	/**
	 * Valida un correo y una contraseña de administrador sin generar token.
	 * <p>
	 * Es un endpoint heredado: el inicio de sesión real del sistema es
	 * {@code POST /api/auth/login} ({@link AuthController}).
	 *
	 * @param correo      correo del administrador
	 * @param contrasenia contraseña en texto plano
	 * @return 200 si son correctos; 400 si el formato del correo es inválido; 401 si no coinciden
	 */
	@PostMapping("/iniciarsesion")
	public ResponseEntity<String> validarSesion(@RequestParam String correo, @RequestParam String contrasenia){
		int status = admServ.validarInicioSesion(correo, contrasenia);
		
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
