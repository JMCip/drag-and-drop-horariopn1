package co.edu.unbosque.sistemahorario.util.exceptions;

import co.edu.unbosque.sistemahorario.util.enums.Dia;

public class LanzadorDeExcepcion {

	public static void verificarEmail(String email) throws InvalidEmailFormatException {

		if (email == null || email.isBlank()) {

			throw new InvalidEmailFormatException("El email no puede estar vacio");
		}

		String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

		if (!email.matches(regex) || !(email.toLowerCase().contains("unbosque.edu.co") || email.toLowerCase().contains("gmail.com"))) {

			throw new InvalidEmailFormatException("El email debe tener arroba y tener formato unbosque.edu.co o gmail.com");
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

	public static void verificarFranjaHoraria(int inicio, int fin, Dia day) throws InvalidTimeSlotException {
		
		if (!day.equals(Dia.SABADO)) {
			if (inicio < 10 && fin <= 12) {
				if (!(inicio >= 7 && inicio <= 10) || !(fin >= 9 && fin <= 12)) 
					throw new InvalidTimeSlotException("Las materias entre semana son de 7 a 12 antes del almuerzo");
				
			} else if(inicio > 13 && fin > 15){
				if (!(inicio >= 13 && inicio <= 20) || !(fin >= 15 && fin <= 22)) {
					throw new InvalidTimeSlotException("Las materias entre semana son de 13 a 22 despues del almuerzo");
					
				}
			} else {
				throw new InvalidTimeSlotException("Las materias entre semana no pueden estar entre las 12 y las 13 (hora de almuerzo)");				
			}

		} else {
			if ((inicio < 8 || inicio % 2 != 0) || (fin > 12 || fin % 2 != 0))
				throw new InvalidTimeSlotException("Las materias de los sabados inician de 8 a 12");
			
		}
	}
	
	public static void verificarNombre(String nombre) throws InvalidNameFormatException {
		if (nombre == null || nombre.isBlank()) {
			throw new InvalidNameFormatException("El nombre no puede ser nulo ni vacío.");
		}
		
		if (nombre.length() < 2) {
			throw new InvalidNameFormatException("El nombre no puede ser menor a 2 caracteres");
		}

		if (!nombre.trim().matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) {
			throw new InvalidNameFormatException("El nombre '" + nombre + "' no es válido. Solo se permiten letras, espacios, tildes y ñ. "
							+ "No se permiten números ni símbolos.");
		}
	}
	
	
}