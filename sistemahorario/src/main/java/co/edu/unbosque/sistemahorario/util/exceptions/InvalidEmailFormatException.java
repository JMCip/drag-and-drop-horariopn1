package co.edu.unbosque.sistemahorario.util.exceptions;

/**
 * Se lanza cuando un correo está vacío o no tiene un formato permitido.
 */
public class InvalidEmailFormatException extends Exception {
	
	public InvalidEmailFormatException(String msg) {
		super(msg);
	}
}
