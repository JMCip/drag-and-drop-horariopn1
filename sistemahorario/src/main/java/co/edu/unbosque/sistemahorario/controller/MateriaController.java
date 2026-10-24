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

import co.edu.unbosque.sistemahorario.dto.MateriaDTO;
import co.edu.unbosque.sistemahorario.service.MateriaService;
import co.edu.unbosque.sistemahorario.util.enums.Carrera;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/materia")
public class MateriaController {
	
	@Autowired
	private MateriaService matServ;
	
	public MateriaController() {
		
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crearMateria (@RequestBody int inicio[] , @RequestBody int fin[], @RequestParam String nombreMateria, @RequestParam Carrera carrera, @RequestParam short semestre){
		
		MateriaDTO nuevo = new MateriaDTO(inicio, fin, nombreMateria, carrera, semestre);
		int status = matServ.create(nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Materia creada con exito", HttpStatus.CREATED);
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
	
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<MateriaDTO>> obtenertodo() {
		List<MateriaDTO> listaMateria = matServ.getAll();
		
		if(listaMateria.isEmpty()) {
			return new ResponseEntity<List<MateriaDTO>>(listaMateria, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<MateriaDTO>>(listaMateria, HttpStatus.ACCEPTED);
	}
	
	
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarMateria(@RequestParam Long id) {
		int status = matServ.deleteById(id);
		if(status==0) {
			return new ResponseEntity<String>("Materia eliminada correctamente!",HttpStatus.OK);
		}
		else if(status==1) {
			return new ResponseEntity<String>("No se encontro la materia",HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<String>("Error interno del servidor",HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@PutMapping("/actualizar")
	public ResponseEntity<String> actualizarMateria(@RequestParam Long id, @RequestBody MateriaDTO data){
		
		int status = matServ.updateById(id, data);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Materia creada con exito", HttpStatus.CREATED);
		case 1:
			return new ResponseEntity<String>("El nombre de la materia no tiene un formato valido", HttpStatus.NOT_ACCEPTABLE);
		case 2:
			return new ResponseEntity<String>("Ya existe una materia con ese nombre", HttpStatus.CONFLICT);
		case 3:
			return new ResponseEntity<String>("El estudiante no esta dentro de un semestre valido", HttpStatus.BAD_REQUEST);
		case 4:
			return new ResponseEntity<String>("No se puede modificar una materia que ya tiene estudiantes inscritos", HttpStatus.CONFLICT);
		case 5:
			return new ResponseEntity<String>("No se encontro una materia con ese id", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}
	
	@PutMapping("/asignarprofesor")
	public ResponseEntity<String> asignarProfesor(@RequestParam Long idProfesor, @RequestParam Long idMateria) {
		int status = matServ.asignarProfesor(idProfesor, idMateria);

		switch(status) {
		case 0:
			return new ResponseEntity<String>("Profesor asignado con exito", HttpStatus.ACCEPTED);
		case 1:
			return new ResponseEntity<String>("La materia ya tiene un profesor asignado", HttpStatus.CONFLICT);
		case 2:
			return new ResponseEntity<String>("El profesor no tiene disponibilidad en el horario de la materia", HttpStatus.CONFLICT);
		case 3:
			return new ResponseEntity<String>("No se encontro el profesor o la materia", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/inscribirestudiante")
	public ResponseEntity<String> inscribirEstudiante(@RequestParam Long idEstudiante, @RequestParam Long idMateria) {
		int status = matServ.inscribirEstudiante(idEstudiante, idMateria);

		switch(status) {
		case 0:
			return new ResponseEntity<String>("Estudiante inscrito con exito", HttpStatus.ACCEPTED);
		case 1:
			return new ResponseEntity<String>("El estudiante ya esta inscrito en esta materia", HttpStatus.CONFLICT);
		case 2:
			return new ResponseEntity<String>("El horario de la materia se cruza con otra materia del estudiante", HttpStatus.CONFLICT);
		case 3:
			return new ResponseEntity<String>("No se encontro el estudiante o la materia", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}



