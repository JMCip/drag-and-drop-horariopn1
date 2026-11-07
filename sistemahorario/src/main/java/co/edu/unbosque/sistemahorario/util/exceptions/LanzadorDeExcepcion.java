package co.edu.unbosque.sistemahorario.util.exceptions;

import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Materia;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.entity.Usuario;

/**
 * Validaciones del sistema, todas estáticas: formato de datos y reglas de horario.
 * <p>
 * Cada método lanza la excepción correspondiente si el dato no cumple; los servicios la
 * capturan y la convierten en un código de error.
 */
public class LanzadorDeExcepcion {

	/**
	 * Valida el horario de una materia, día por día (lunes a viernes).
	 * <p>
	 * Para cada día con clase se exige: que la hora de fin sea mayor que la de inicio, que la clase
	 * dure máximo 4 horas y que quede dentro de la mañana (7 a 13) o de la tarde (14 a 22), es decir,
	 * sin cruzar la hora de almuerzo. Los días con {@code -1} en inicio y fin se omiten.
	 *
	 * @param inicio hora de inicio por día (5 valores)
	 * @param fin    hora de fin por día (5 valores)
	 * @throws InvalidTimeSlotException si algún día incumple una de las reglas
	 */
	public static void verificarFranjaHoraria(int[] inicio, int[] fin) throws InvalidTimeSlotException {

		for (int i = 0; i < 5; i++) {
			if (inicio[i] == -1 && fin[i] == -1) {
				continue;
			}

			if (fin[i] - inicio[i] < 1) {
				throw new InvalidTimeSlotException("La hora de fin debe ser mayor a la de inicio");
			}

			if (fin[i] - inicio[i] > 4) {
				throw new InvalidTimeSlotException("Las materias no pueden durar mas de 4 horas");
			}

			boolean enLaManana = inicio[i] >= 7 && fin[i] <= 13;
			boolean enLaTarde = inicio[i] >= 14 && fin[i] <= 22;

			if (!enLaManana && !enLaTarde) {
				throw new InvalidTimeSlotException(
						"Las materias son de 7 a 13 o de 14 a 22, no pueden cruzar la hora de almuerzo");
			}
		}
	}

	/**
	 * Indica si dos materias se solapan en algún día.
	 * <p>
	 * Dos clases se cruzan cuando empiezan antes de que la otra termine y viceversa. Si una termina
	 * justo cuando la otra empieza, no hay cruce. Los días con {@code -1} se ignoran.
	 *
	 * @param a primera materia
	 * @param b segunda materia
	 * @return {@code true} si hay cruce en al menos un día
	 */
	public static boolean hayCruce(Materia a, Materia b) {

		for (int i = 0; i < 5; i++) {
			if (a.getInicio()[i] != -1 && b.getInicio()[i] != -1
					&& a.getInicio()[i] < b.getFin()[i] && b.getInicio()[i] < a.getFin()[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Comprueba que una materia no choque con las que el usuario ya tiene (las que dicta, si es
	 * profesor, o las que cursa, si es estudiante). Para un administrador no hace nada.
	 *
	 * @param user    profesor o estudiante a revisar
	 * @param materia materia que se quiere agregar
	 * @throws InvalidTimeSlotException si hay cruce con alguna de sus materias
	 */
	public static void verificarDisponibilidad(Usuario user, Materia materia) throws InvalidTimeSlotException {

		if (user instanceof Profesor) {
			Profesor prof = (Profesor) user;
			for (Materia profMateria : prof.getListaMaterias()) {
				if (hayCruce(profMateria, materia)) {
					throw new InvalidTimeSlotException("Esa materia choca con el horario del profesor en algun punto");
				}
			}
		}

		if(user instanceof Estudiante) {
			Estudiante est = (Estudiante) user;
			for (Materia estMateria : est.getListaMateriasInscritas()) {
				if (hayCruce(estMateria, materia)) {
					throw new InvalidTimeSlotException("Esa materia choca con el horario del estudiante en algun punto");
				}
			}
		}
	}

	/**
	 * Valida el nombre de una persona: no vacío, mínimo 2 caracteres y solo letras, espacios,
	 * tildes, diéresis y ñ (sin números ni símbolos).
	 *
	 * @param nombre nombre a validar
	 * @throws InvalidNameFormatException si no cumple
	 */
	public static void verificarNombre(String nombre) throws InvalidNameFormatException {

		if (nombre == null || nombre.isBlank()) {
			throw new InvalidNameFormatException("El nombre no puede ser nulo ni vacío.");
		}

		if (nombre.length() < 2) {
			throw new InvalidNameFormatException("El nombre no puede ser menor a 2 caracteres");
		}

		String regex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$";

		if (!nombre.trim().matches(regex)) {
			throw new InvalidNameFormatException(
					"El nombre '" + nombre + "' no es válido. Solo se permiten letras, espacios, tildes y ñ. "
							+ "No se permiten números ni símbolos.");
		}
	}

	/**
	 * Valida un texto general, como el nombre de una materia: solo letras, números y espacios.
	 *
	 * @param texto texto a validar
	 * @throws InvalidTextFormatException si es nulo o tiene caracteres no permitidos
	 */
	public static void verificarTexto(String texto) throws InvalidTextFormatException {

		if (texto == null) {
			throw new InvalidTextFormatException("El texto no puede estar vacio");
		}

		String regex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ0-9 ]+$";

		if (!texto.matches(regex)) {
			throw new InvalidTextFormatException("El texto solo debe contener letras y numeros");
		}
	}

	/**
	 * Valida un correo: no vacío, con formato de correo y del dominio {@code unbosque.edu.co} o {@code gmail.com}.
	 *
	 * @param email correo a validar
	 * @throws InvalidEmailFormatException si no cumple
	 */
	public static void verificarEmail(String email) throws InvalidEmailFormatException {

		if (email == null || email.isBlank()) {

			throw new InvalidEmailFormatException("El email no puede estar vacio");
		}

		String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

		if (!email.matches(regex)
				|| !(email.toLowerCase().contains("unbosque.edu.co") || email.toLowerCase().contains("gmail.com"))) {

			throw new InvalidEmailFormatException(
					"El email debe tener arroba y tener formato unbosque.edu.co o gmail.com");
		}
	}

	/**
	 * Valida que la contraseña sea segura: tiene mínimo 8 caracteres e incluye al menos una letra,
	 * un número y un símbolo. (El mensaje de error menciona las mayúsculas, pero la expresión regular
	 * actual no las exige.)
	 *
	 * @param pw contraseña a validar
	 * @throws InvalidPasswordException si no cumple
	 */
	public static void verificarContrasenia(String pw) throws InvalidPasswordException {

		if (pw == null || pw.isBlank())
			throw new InvalidPasswordException("Contraseña no puede estar vacia");

		String regex = "^(?=.*[a-zA-ZñÑ])(?=.*\\d)(?=.*[\\W_]).+$";

		if (!pw.matches(regex) || pw.length() < 8)
			throw new InvalidPasswordException(
					"Contraseña demasiado debil, debe contener minimo 8 caracterez, numeros, mayusculas y simbolos");
	}

}