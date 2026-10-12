package co.edu.unbosque.sistemahorario.dto;

import java.util.Objects;

public class UsuarioDTO {
	
	private long id;
	private String correo;
	private String contrasenia;

	public UsuarioDTO() {
		super();
	}

	public UsuarioDTO(String correo, String contrasenia) {
		super();
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
		UsuarioDTO other = (UsuarioDTO) obj;
		return Objects.equals(contrasenia, other.contrasenia) && Objects.equals(correo, other.correo) && id == other.id;

	}
}
