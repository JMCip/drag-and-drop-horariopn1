package co.edu.unbosque.sistemahorario.util.exceptions;

/**
 * Se lanza cuando una contraseña está vacía o es demasiado débil.
 */
public class InvalidPasswordException extends Exception {

	public InvalidPasswordException(String msg) {
		super(msg);
	}
}
