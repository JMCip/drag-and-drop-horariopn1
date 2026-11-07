package co.edu.unbosque.sistemahorario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.sistemahorario.dto.MateriaDTO;
import co.edu.unbosque.sistemahorario.entity.Admin;
import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Usuario;
import co.edu.unbosque.sistemahorario.service.MateriaService;
import co.edu.unbosque.sistemahorario.service.UserDetailsServiceImpl;

@RestController
@RequestMapping("/materia")
public class MateriaController {
	
	@Autowired
	private MateriaService matServ;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	public MateriaController() {
		
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crearMateria (@RequestParam int[] inicio, @RequestParam int[] fin, @RequestParam Long idAsignatura, @RequestParam(defaultValue = "1") int grupo){

		MateriaDTO nuevo = new MateriaDTO(inicio, fin, idAsignatura, grupo);
		int status = matServ.create(nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Materia creada con exito", HttpStatus.CREATED);
		case 1:
			return new ResponseEntity<String>("La materia no existe en el catalogo", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<String>("Ya existe ese grupo de la materia", HttpStatus.CONFLICT);
		case 4:
			return new ResponseEntity<String>("El horario de la materia no es valido", HttpStatus.BAD_REQUEST);
		case 5:
			return new ResponseEntity<String>("El numero de grupo no es valido", HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	
	
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<MateriaDTO>> obtenertodo(Authentication auth) {
		Usuario usuario = userDetailsService.buscarPorCorreo(auth.getName());
		List<MateriaDTO> listaMateria = usuario instanceof Estudiante ? matServ.getAllParaEstudiante(usuario.getId()) : matServ.getAll();
		
		return new ResponseEntity<List<MateriaDTO>>(listaMateria, HttpStatus.OK);
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
	public ResponseEntity<String> actualizarMateria(@RequestParam int[] inicio, @RequestParam int[] fin, @RequestParam Long idAsignatura, @RequestParam(defaultValue = "1") int grupo, @RequestParam Long id){

		MateriaDTO nuevo = new MateriaDTO(inicio, fin, idAsignatura, grupo);
		int status = matServ.updateById(id, nuevo);
		
		switch(status) {
		case 0:
			return new ResponseEntity<String>("Materia actualizada con exito", HttpStatus.OK);
		case 1:
			return new ResponseEntity<String>("La materia no existe en el catalogo", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<String>("Ya existe ese grupo de la materia", HttpStatus.CONFLICT);
		case 4:
			return new ResponseEntity<String>("No se puede modificar una materia que ya tiene estudiantes inscritos", HttpStatus.CONFLICT);
		case 5:
			return new ResponseEntity<String>("No se encontro una materia con ese id", HttpStatus.NOT_FOUND);
		case 6:
			return new ResponseEntity<String>("El horario de la materia no es valido", HttpStatus.BAD_REQUEST);
		case 7:
			return new ResponseEntity<String>("El numero de grupo no es valido", HttpStatus.BAD_REQUEST);
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
			return new ResponseEntity<String>("Ese profesor ya esta asignado a la materia", HttpStatus.CONFLICT);
		case 2:
			return new ResponseEntity<String>("El profesor no tiene disponibilidad en el horario de la materia", HttpStatus.CONFLICT);
		case 3:
			return new ResponseEntity<String>("No se encontro el profesor o la materia", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/quitarprofesor")
	public ResponseEntity<String> quitarProfesor(@RequestParam Long idMateria) {
		int status = matServ.quitarProfesor(idMateria);

		switch(status) {
		case 0:
			return new ResponseEntity<String>("Profesor quitado de la materia", HttpStatus.OK);
		case 1:
			return new ResponseEntity<String>("La materia no tiene profesor asignado", HttpStatus.CONFLICT);
		case 2:
			return new ResponseEntity<String>("No se encontro la materia", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/inscribirestudiante")
	public ResponseEntity<String> inscribirEstudiante(@RequestParam Long idEstudiante, @RequestParam Long idMateria, Authentication auth) {
		if(!puedeOperarSobre(idEstudiante, auth)) {
			return new ResponseEntity<String>("Solo puedes modificar tu propio horario", HttpStatus.FORBIDDEN);
		}

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
		case 4:
			return new ResponseEntity<String>("El estudiante ya esta inscrito en otro grupo de esta materia", HttpStatus.CONFLICT);
		case 5:
			return new ResponseEntity<String>("La materia no corresponde a la carrera o al semestre del estudiante", HttpStatus.CONFLICT);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/desinscribirestudiante")
	public ResponseEntity<String> desinscribirEstudiante(@RequestParam Long idEstudiante, @RequestParam Long idMateria, Authentication auth) {
		if(!puedeOperarSobre(idEstudiante, auth)) {
			return new ResponseEntity<String>("Solo puedes modificar tu propio horario", HttpStatus.FORBIDDEN);
		}

		int status = matServ.desinscribirEstudiante(idEstudiante, idMateria);

		switch(status) {
		case 0:
			return new ResponseEntity<String>("Estudiante desinscrito con exito", HttpStatus.OK);
		case 1:
			return new ResponseEntity<String>("El estudiante no esta inscrito en esta materia", HttpStatus.CONFLICT);
		case 2:
			return new ResponseEntity<String>("No se encontro el estudiante o la materia", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/moverestudiante")
	public ResponseEntity<String> moverEstudiante(@RequestParam Long idEstudiante, @RequestParam Long idMateriaOrigen, @RequestParam Long idMateriaDestino) {
		int status = matServ.moverEstudiante(idEstudiante, idMateriaOrigen, idMateriaDestino);

		switch(status) {
		case 0:
			return new ResponseEntity<String>("Estudiante movido con exito", HttpStatus.OK);
		case 1:
			return new ResponseEntity<String>("El estudiante no esta inscrito en la materia de origen", HttpStatus.CONFLICT);
		case 2:
			return new ResponseEntity<String>("Solo se puede mover entre grupos de la misma materia", HttpStatus.CONFLICT);
		case 3:
			return new ResponseEntity<String>("El estudiante ya esta inscrito en el grupo destino", HttpStatus.CONFLICT);
		case 4:
			return new ResponseEntity<String>("El grupo destino se cruza con otra materia del estudiante", HttpStatus.CONFLICT);
		case 5:
			return new ResponseEntity<String>("No se encontro el estudiante o la materia", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<String>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean puedeOperarSobre(Long idEstudiante, Authentication auth) {
		Usuario usuario = userDetailsService.buscarPorCorreo(auth.getName());
		return usuario instanceof Admin || usuario.getId() == idEstudiante;
	}
	
}



