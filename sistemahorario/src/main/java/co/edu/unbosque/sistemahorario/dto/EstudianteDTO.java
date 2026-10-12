package co.edu.unbosque.sistemahorario.dto;

import java.util.ArrayList;
import java.util.Objects;

public class EstudianteDTO extends UsuarioDTO{
	
	private ArrayList<Long> listaIdMateriasInscritas;
	private String nombre;
	
	public EstudianteDTO() {
		super();
	}
	
	public EstudianteDTO(ArrayList<Long> listaIdMateriasInscritas, String nombre) {
		super();
		this.listaIdMateriasInscritas = listaIdMateriasInscritas;
		this.nombre = nombre;
	}

	public EstudianteDTO(String correo, String contrasenia) {
		super(correo, contrasenia);
	}
	
	public EstudianteDTO(String correo, String contrasenia, ArrayList<Long> listaIdMateriasInscritas, String nombre) {
		super(correo, contrasenia);
		this.listaIdMateriasInscritas = listaIdMateriasInscritas;
		this.nombre = nombre;		
	}
	
	public EstudianteDTO(String correo, String contrasenia, String nombre) {
		super(correo, contrasenia);
		this.nombre = nombre;		
	}

	public ArrayList<Long> getListaIdMateriasInscritas() {
		return listaIdMateriasInscritas;
	}

	public void setListaIdMateriasInscritas(ArrayList<Long> listaIdMateriasInscritas) {
		this.listaIdMateriasInscritas = listaIdMateriasInscritas;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Override
	public String toString() {
		return "EstudianteDTO" + 
				super.toString() + 
				"\nLista id materias inscritas: " + listaIdMateriasInscritas + 
				"\nNombre: " + nombre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(listaIdMateriasInscritas, nombre);
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
		EstudianteDTO other = (EstudianteDTO) obj;
		return Objects.equals(listaIdMateriasInscritas, other.listaIdMateriasInscritas)
				&& Objects.equals(nombre, other.nombre);
	}
}