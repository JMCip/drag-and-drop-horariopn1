package co.edu.unbosque.sistemahorario.controller;

import java.util.List;

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

@RestController
@RequestMapping("/profesor")
public class ProfesorController {
	
	private ProfesorService profServ;
	
	public ProfesorController()
	{
		
	}
	

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
			return new ResponseEntity<String>("El formato de la contraseña no es valido", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<ProfesorDTO>> obtenertodo(){
		List<ProfesorDTO> listaProfesor = profServ.getAll();
		
		if(listaProfesor.isEmpty()) {
			return new ResponseEntity<List<ProfesorDTO>>(listaProfesor, HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<ProfesorDTO>>(listaProfesor, HttpStatus.ACCEPTED);
	}
	
	
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
	
	
	
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarProfesor(@RequestParam String nombre, @RequestParam String contrasenia, @RequestParam String correo, @RequestParam Long id) {
		
		ProfesorDTO nuevo = new ProfesorDTO(correo, contrasenia, nombre);
		int status = profServ.updateById(id, nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Profesor actualizado con exito", HttpStatus.CREATED);
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
			return new ResponseEntity<String>("Profesor no encontrado", HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<String>("El formato de la contraseña no es valido", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/iniciarsesion")
	public ResponseEntity<String> validarSesion(@RequestParam String nombre, @RequestParam String constrasenia, @RequestParam String correo, @RequestParam Long id){
		int status = profServ.validarInicioSesion(correo, constrasenia);
		
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
