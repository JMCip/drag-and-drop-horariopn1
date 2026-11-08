package co.edu.unbosque.sistemahorario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.sistemahorario.dto.AsignaturaDTO;
import co.edu.unbosque.sistemahorario.service.AsignaturaService;

/**
 * Controlador REST del catálogo de asignaturas ({@code /asignatura}).
 * <p>
 * Solo permite consultar. Según {@code SecurityConfig}, solo el rol ADMIN puede usarlo.
 */
@RestController
@RequestMapping("/asignatura")
public class AsignaturaController {

	@Autowired
	private AsignaturaService asigServ;

	public AsignaturaController() {

	}

	/**
	 * Lista todas las asignaturas del catálogo con sus planes de estudio.
	 *
	 * @return 200 con la lista de {@link AsignaturaDTO}
	 */
	@GetMapping("/mostrartodo")
	public ResponseEntity<List<AsignaturaDTO>> obtenertodo() {
		return new ResponseEntity<List<AsignaturaDTO>>(asigServ.getAll(), HttpStatus.OK);
	}
}
