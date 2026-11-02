package co.edu.unbosque.sistemahorario.util.exceptions;

import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Materia;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.entity.Usuario;

public class LanzadorDeExcepcion {

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

	public static boolean hayCruce(Materia a, Materia b) {

		for (int i = 0; i < 5; i++) {
			if (a.getInicio()[i] != -1 && b.getInicio()[i] != -1
					&& a.getInicio()[i] < b.getFin()[i] && b.getInicio()[i] < a.getFin()[i]) {
				return true;
			}
		}
		return false;
	}

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

	public static void verificarTexto(String texto) throws InvalidTextFormatException {

		if (texto == null) {
			throw new InvalidTextFormatException("El texto no puede estar vacio");
		}

		String regex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ0-9 ]+$";

		if (!texto.matches(regex)) {
			throw new InvalidTextFormatException("El texto solo debe contener letras y numeros");
		}
	}

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

	public static void verificarContrasenia(String pw) throws InvalidPasswordException {

		if (pw == null || pw.isBlank())
			throw new InvalidPasswordException("Contraseña no puede estar vacia");

		String regex = "^(?=.*[a-zA-ZñÑ])(?=.*\\d)(?=.*[\\W_]).+$";

		if (!pw.matches(regex) || pw.length() < 8)
			throw new InvalidPasswordException(
					"Contraseña demasiado debil, debe contener minimo 8 caracterez, numeros, mayusculas y simbolos");
	}

}