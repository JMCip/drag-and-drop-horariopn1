package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "profesor")
public class Profesor extends Usuario{
	
	@JsonIgnore
	@OneToMany(mappedBy = "materia", cascade = CascadeType.ALL)
	private ArrayList<Materia> listaMaterias;

	private String nombre;
	
	public Profesor() {
		super();
	}

	public Profesor(ArrayList<Materia> listaMaterias, String nombre) {
		super();
		this.listaMaterias = listaMaterias;
		this.nombre = nombre;
	}

	public Profesor(String correo, String contrasenia) {
		super(correo, contrasenia);
	}
	
	public Profesor(String correo, String contrasenia, ArrayList<Materia> listaMaterias, String nombre) {
		super(correo, contrasenia);
		this.listaMaterias = listaMaterias;
		this.nombre = nombre;
	}
	
	public Profesor(String correo, String contrasenia, String nombre) {
		super(correo, contrasenia);
		this.nombre = nombre;
	}

	public ArrayList<Materia> getListaMaterias() {
		return listaMaterias;
	}

	public void setListaMaterias(ArrayList<Materia> listaMaterias) {
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
				"\nLista materias: " + listaMaterias + 
				"\nNombre: " + nombre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(listaMaterias, nombre);
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
		return Objects.equals(listaMaterias, other.listaMaterias) && Objects.equals(nombre, other.nombre);
	}
}
