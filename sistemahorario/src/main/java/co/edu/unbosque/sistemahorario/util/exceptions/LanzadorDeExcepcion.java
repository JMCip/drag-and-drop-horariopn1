package co.edu.unbosque.sistemahorario.util.exceptions;

import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.entity.Materia;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.entity.Usuario;

public class LanzadorDeExcepcion {

	public static void verificarFranjaHoraria(int[] inicio, int[] fin) throws InvalidTimeSlotException {

		for (int i = 0; i < 5; i++) {
			if (Math.abs(fin[i] - inicio[i]) > 4) {
				throw new InvalidTimeSlotException("Las materias no pueden durar mas de 4 horas");
			}

			if (i != 5) {
				if (inicio[i] >= 7 && fin[i] <= 12) {
					if (fin[i] - inicio[i] < 1)
						throw new InvalidTimeSlotException(
								"Las materias entre semana son de 7 a 12 antes del almuerzo");

				} else if (inicio[i] >= 13 && fin[i] <= 22) {
					if (fin[i] - inicio[i] < 1) {
						throw new InvalidTimeSlotException(
								"Las materias entre semana son de 13 a 22 despues del almuerzo");

					}
				} else {
					throw new InvalidTimeSlotException(
							"Las materias entre semana no pueden estar entre las 12 y las 13 (hora de almuerzo)");
				}

			} else {
				if ((inicio[i] < 8 || inicio[i] % 2 != 0) || (fin[i] > 12 || fin[i] % 2 != 0))
					throw new InvalidTimeSlotException("Las materias de los sabados inician de 8 a 12");

			}
		}
	}

	public static void verificarDisponibilidad(Usuario user, Materia materia) throws InvalidTimeSlotException {

		if (user instanceof Profesor) {
			Profesor prof = (Profesor) user;
			for (Materia profMateria : prof.getListaMaterias()) {
				int[] inicio = profMateria.getInicio();
				int[] fin = profMateria.getFin();
				for (int i = 0; i < 5; i++) {
					if (inicio[i] != -1 && materia.getInicio()[i] != -1) {
						if (inicio[i] == materia.getInicio()[i] || fin[i] == materia.getFin()[i]) {
							throw new InvalidTimeSlotException("Esa materia choca con el horario del profesor en algun punto");
						}
					}
				}
			}
		}
		
		if(user instanceof Estudiante) {
			Estudiante est = (Estudiante) user;
			for (Materia estMateria : est.getListaMateriasInscritas()) {
				int[] inicio = estMateria.getInicio();
				int[] fin = estMateria.getFin();
				for (int i = 0; i < 5; i++) {
					if (inicio[i] != -1 && materia.getInicio()[i] != -1) {
						if (inicio[i] == materia.getInicio()[i] || fin[i] == materia.getFin()[i]) {
							throw new InvalidTimeSlotException("Esa materia choca con el horario del estudiante en algun punto");
						}
					}
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