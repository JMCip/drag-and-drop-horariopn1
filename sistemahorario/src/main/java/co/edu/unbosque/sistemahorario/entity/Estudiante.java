package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiante")
public class Estudiante extends Usuario{
	
	@JsonIgnore
	@OneToMany(mappedBy = "materia", cascade = CascadeType.ALL)
	private ArrayList<Materia> listaMateriasInscritas;
	
	private String nombre;
	
	public Estudiante() {
		super();
	}
	
	public Estudiante(ArrayList<Materia> listaMateriasInscritas, String nombre) {
		super();
		this.listaMateriasInscritas = listaMateriasInscritas;
		this.nombre = nombre;
	}

	public Estudiante(String correo, String contrasenia) {
		super(correo, contrasenia);
	}
	
	public Estudiante(String correo, String contrasenia, ArrayList<Materia> listaMateriasInscritas, String nombre) {
		super(correo, contrasenia);
		this.listaMateriasInscritas = listaMateriasInscritas;
		this.nombre = nombre;		
	}
	
	public Estudiante(String correo, String contrasenia, String nombre) {
		super(correo, contrasenia);
		this.nombre = nombre;		
	}

	public ArrayList<Materia> getListaMateriasInscritas() {
		return listaMateriasInscritas;
	}

	public void setListaMateriasInscritas(ArrayList<Materia> listaMateriasInscritas) {
		this.listaMateriasInscritas = listaMateriasInscritas;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Estudiante" + 
				super.toString() + 
				"\nLista materias inscritas: " + listaMateriasInscritas + 
				"\nNombre: " + nombre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(listaMateriasInscritas, nombre);
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
		Estudiante other = (Estudiante) obj;
		return Objects.equals(listaMateriasInscritas, other.listaMateriasInscritas)
				&& Objects.equals(nombre, other.nombre);
	}
}
