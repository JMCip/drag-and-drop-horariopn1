package co.edu.unbosque.sistemahorario.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
public class Admin extends Usuario{
	
	public Admin() {
		super();
	}

	public Admin(String correo, String contrasenia) {
		super(correo, contrasenia);
	}

	@Override
	public String toString() {
		return "Admin " +
				super.toString();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}