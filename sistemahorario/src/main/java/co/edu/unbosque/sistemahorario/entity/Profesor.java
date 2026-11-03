package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Profesor (tabla {@code profesor}). Puede dictar varias materias, siempre que sus horarios no se crucen.
 */
@Entity
@Table(name = "profesor")
public class Profesor extends Usuario{
	
	/**
	 * Materias que dicta. Es el lado inverso de la relación: la columna {@code profesor_id} vive en {@link Materia}.
	 * Se ignora al serializar a JSON para evitar referencias circulares.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "profesorAsignado")
	private List<Materia> listaMaterias = new ArrayList<>();

	/**
	 * Nombre completo del profesor.
	 */
	private String nombre;
	
	public Profesor() {
		super();
	}

	public Profesor(List<Materia> listaMaterias, String nombre) {
		super();
		this.listaMaterias = listaMaterias;
		this.nombre = nombre;
	}

	public Profesor(String correo, String contrasenia) {
		super(correo, contrasenia);
	}
	
	public Profesor(String correo, String contrasenia, List<Materia> listaMaterias, String nombre) {
		super(correo, contrasenia);
		this.listaMaterias = listaMaterias;
		this.nombre = nombre;
	}
	
	public Profesor(String correo, String contrasenia, String nombre) {
		super(correo, contrasenia);
		this.nombre = nombre;
	}

	public List<Materia> getListaMaterias() {
		return listaMaterias;
	}

	public void setListaMaterias(List<Materia> listaMaterias) {
		this.listaMaterias = listaMaterias;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Profesor" + 
				super.toString() + 
				"\nNombre: " + nombre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(nombre);
		return result;
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
		Profesor other = (Profesor) obj;
		return Objects.equals(nombre, other.nombre);
	}
}
