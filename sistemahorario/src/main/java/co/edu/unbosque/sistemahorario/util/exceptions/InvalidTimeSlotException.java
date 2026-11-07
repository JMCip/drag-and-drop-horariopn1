package co.edu.unbosque.sistemahorario.util.exceptions;

/**
 * Se lanza cuando un horario no es válido o se cruza con otro.
 */
public class InvalidTimeSlotException extends Exception {
	
	public InvalidTimeSlotException(String msg) {
		super(msg);
	}
}
