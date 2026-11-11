package co.edu.unbosque.sistemahorario.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Entidad base de todos los usuarios del sistema (tabla {@code usuario}).
 * <p>
 * Usa herencia {@code JOINED}: los datos comunes (id, correo y contraseña) están en esta tabla
 * y cada subclase ({@link Admin}, {@link Profesor}, {@link Estudiante}) guarda sus datos propios
 * en su propia tabla, enlazada por el mismo id. Es abstracta porque nunca existe un "usuario" genérico.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuario")
public abstract class Usuario {

	/**
	 * Identificador autogenerado por la base de datos.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	/**
	 * Correo del usuario. Es único en todo el sistema y se usa como nombre de usuario para iniciar sesión.
	 */
	private String correo;
	/**
	 * Contraseña cifrada con BCrypt. Nunca se guarda en texto plano.
	 */
	private String contrasenia;
	
	public Usuario() {
	}
		
	public Usuario(String correo, String contrasenia) {
		this.correo = correo;
		this.contrasenia = contrasenia;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	@Override
	public String toString() {
		return "Id: " + id + 
				"\nCorreo: " + correo + 
				"\nContrasenia: " + contrasenia;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contrasenia, correo, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(contrasenia, other.contrasenia) && Objects.equals(correo, other.correo) && id == other.id;
	}
}
