package co.edu.unbosque.sistemahorario.util.exceptions;

/**
 * Se lanza cuando un texto (por ejemplo el nombre de una materia) tiene caracteres no permitidos.
 */
public class InvalidTextFormatException extends Exception {

	public InvalidTextFormatException(String msg) {
		super(msg);
	}
}
