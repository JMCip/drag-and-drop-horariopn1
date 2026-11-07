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

/**
 * Controlador REST de materias ({@code /materia}).
 * <p>
 * En este sistema cada {@code Materia} representa <b>un grupo concreto</b> de una asignatura
 * (la pareja nombre + grupo es única). Además de su CRUD, este controlador expone las
 * operaciones de horario: asignar profesor, inscribir, desinscribir y mover estudiantes.
 * <p>
 * Permisos: listar lo puede hacer cualquier usuario autenticado; inscribir y desinscribir,
 * ADMIN y ESTUDIANTE (el estudiante solo sobre sí mismo); todo lo demás, solo ADMIN.
 */
@RestController
@RequestMapping("/materia")
public class MateriaController {
	
	@Autowired
	private MateriaService matServ;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	public MateriaController() {
		
	}
	
	/**
	 * Crea un grupo de una materia.
	 *
	 * @param inicio        hora de inicio de la clase de lunes a viernes (5 valores; {@code -1} = sin clase ese día)
	 * @param fin           hora de fin de la clase de lunes a viernes (5 valores; {@code -1} = sin clase ese día)
	 * @param nombreMateria nombre de la materia
	 * @param carrera       carrera a la que pertenece
	 * @param semestre      semestre de la materia (1 a 10)
	 * @param grupo         número de grupo (por defecto 1)
	 * @return 201 si se creó; 406 si el nombre tiene formato inválido; 409 si ya existe ese grupo
	 *         de la materia; 400 si el semestre, el horario o el grupo no son válidos; 500 en otro caso
	 */
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
	
	
	/**
	 * Lista todas las materias (grupos) con su profesor asignado y los ids de los estudiantes inscritos.
	 *
	 * @return 200 con la lista de {@link MateriaDTO}
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<MateriaDTO>> obtenertodo(Authentication auth) {
		Usuario usuario = userDetailsService.buscarPorCorreo(auth.getName());
		List<MateriaDTO> listaMateria = usuario instanceof Estudiante ? matServ.getAllParaEstudiante(usuario.getId()) : matServ.getAll();
		
		return new ResponseEntity<List<MateriaDTO>>(listaMateria, HttpStatus.OK);
	}
	
	
	/**
	 * Elimina una materia (grupo) por su id.
	 *
	 * @param id identificador de la materia
	 * @return 200 si se eliminó; 404 si no existe; 500 en cualquier otro caso
	 */
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
	
	
	/**
	 * Actualiza una materia (grupo).
	 * <p>
	 * No se permite modificar una materia que ya tiene estudiantes inscritos, para no dejarles
	 * un horario distinto al que eligieron. Si cambia el horario, se le quita el profesor asignado.
	 *
	 * @param inicio        nueva hora de inicio por día (5 valores; {@code -1} = sin clase)
	 * @param fin           nueva hora de fin por día (5 valores; {@code -1} = sin clase)
	 * @param nombreMateria nuevo nombre
	 * @param carrera       nueva carrera
	 * @param semestre      nuevo semestre (1 a 10)
	 * @param grupo         nuevo número de grupo
	 * @param id            identificador de la materia a modificar
	 * @return 200 si se actualizó; 406 si el nombre es inválido; 409 si ya existe ese grupo o si la
	 *         materia tiene estudiantes inscritos; 400 si semestre, horario o grupo no son válidos;
	 *         404 si no existe; 500 en otro caso
	 */
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
	
	/**
	 * Asigna un profesor a una materia.
	 *
	 * @param idProfesor identificador del profesor
	 * @param idMateria  identificador de la materia
	 * @return 202 si se asignó; 409 si la materia ya tiene profesor o si el horario del profesor
	 *         se cruza con el de la materia; 404 si no se encontró el profesor o la materia; 500 en otro caso
	 */
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

	/**
	 * Inscribe un estudiante en una materia (grupo).
	 * <p>
	 * Un ESTUDIANTE solo puede inscribirse a sí mismo; el ADMIN puede inscribir a cualquiera.
	 * Se rechaza si ya está inscrito, si ya cursa otro grupo de la misma materia o si el horario
	 * se cruza con otra de sus materias.
	 *
	 * @param idEstudiante identificador del estudiante
	 * @param idMateria    identificador de la materia
	 * @param auth         usuario autenticado (lo inyecta Spring Security a partir del token)
	 * @return 202 si quedó inscrito; 409 si hay conflicto; 403 si un estudiante intenta modificar
	 *         el horario de otro; 404 si no se encontró el estudiante o la materia
	 */
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

	/**
	 * Quita la inscripción de un estudiante en una materia.
	 * <p>
	 * Un ESTUDIANTE solo puede modificar su propio horario; el ADMIN puede modificar el de cualquiera.
	 *
	 * @param idEstudiante identificador del estudiante
	 * @param idMateria    identificador de la materia
	 * @param auth         usuario autenticado (lo inyecta Spring Security a partir del token)
	 * @return 200 si se quitó la inscripción; 409 si el estudiante no estaba inscrito;
	 *         403 si intenta modificar el horario de otro; 404 si no se encontró el estudiante o la materia
	 */
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

	/**
	 * Mueve un estudiante de un grupo a otro de la <b>misma</b> materia (reubicación).
	 * <p>
	 * Solo la puede usar el ADMIN. Se rechaza si el estudiante no está en el grupo origen, si
	 * los grupos no son de la misma materia, si ya está en el destino o si el horario del destino
	 * se cruza con otra materia suya.
	 *
	 * @param idEstudiante     identificador del estudiante
	 * @param idMateriaOrigen  grupo del que sale
	 * @param idMateriaDestino grupo al que entra
	 * @return 200 si se movió; 409 si hay conflicto; 404 si no se encontró alguno de los elementos
	 */
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

	/**
	 * Indica si el usuario autenticado puede modificar el horario del estudiante indicado.
	 *
	 * @param idEstudiante estudiante sobre el que se quiere operar
	 * @param auth         usuario autenticado
	 * @return {@code true} si es ADMIN o si es el propio estudiante; {@code false} en otro caso
	 */
	private boolean puedeOperarSobre(Long idEstudiante, Authentication auth) {
		Usuario usuario = userDetailsService.buscarPorCorreo(auth.getName());
		return usuario instanceof Admin || usuario.getId() == idEstudiante;
	}
	
}



