package co.edu.unbosque.sistemahorario.util.exceptions;

/**
 * Se lanza cuando un nombre está vacío, es muy corto o tiene caracteres no permitidos.
 */
public class InvalidNameFormatException extends Exception {

	public InvalidNameFormatException(String msg) {
		super(msg);
	}
}
