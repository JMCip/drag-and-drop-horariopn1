package co.edu.unbosque.sistemahorario.dto;

import java.util.ArrayList;
import java.util.Objects;

public class ProfesorDTO extends UsuarioDTO{
	
	private ArrayList<Long> listaIdMaterias;
	private String nombre;
	
	public ProfesorDTO() {
		super();
	}

	public ProfesorDTO(ArrayList<Long> listaIdMaterias, String nombre) {
		super();
		this.listaIdMaterias = listaIdMaterias;
		this.nombre = nombre;
	}

	public ProfesorDTO(String correo, String contrasenia) {
		super(correo, contrasenia);
	}
	
	public ProfesorDTO(String correo, String contrasenia,ArrayList<Long> listaIdMaterias, String nombre) {
		super(correo, contrasenia);
		this.listaIdMaterias = listaIdMaterias;
		this.nombre = nombre;
	}
	
	public ProfesorDTO(String correo, String contrasenia, String nombre) {
		super(correo, contrasenia);
		this.nombre = nombre;
	}

	public ArrayList<Long> getListaIdMaterias() {
		return listaIdMaterias;
	}

	public void setListaIdMaterias(ArrayList<Long> listaIdMaterias) {
		this.listaIdMaterias = listaIdMaterias;
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
				"\nLista id materias: " + listaIdMaterias + 
				"\nNombre: " + nombre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(listaIdMaterias, nombre);
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
		ProfesorDTO other = (ProfesorDTO) obj;
		return Objects.equals(listaIdMaterias, other.listaIdMaterias) && Objects.equals(nombre, other.nombre);
	}
}
