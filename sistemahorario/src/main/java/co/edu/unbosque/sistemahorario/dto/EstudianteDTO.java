package co.edu.unbosque.sistemahorario.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;

public class EstudianteDTO extends UsuarioDTO{
	
	private List<Long> listaIdMateriasInscritas = new ArrayList<>();
	private String nombre;
	private Carrera carrera;
	private Short semestre;
	
	public EstudianteDTO() {
		super();
	}
	
	public EstudianteDTO(List<Long> listaIdMateriasInscritas, String nombre) {
		super();
		this.listaIdMateriasInscritas = listaIdMateriasInscritas;
		this.nombre = nombre;
	}

	public EstudianteDTO(String correo, String contrasenia) {
		super(correo, contrasenia);
	}
	
	public EstudianteDTO(String correo, String contrasenia, List<Long> listaIdMateriasInscritas, String nombre) {
		super(correo, contrasenia);
		this.listaIdMateriasInscritas = listaIdMateriasInscritas;
		this.nombre = nombre;		
	}
	
	public EstudianteDTO(String correo, String contrasenia, String nombre) {
		super(correo, contrasenia);
		this.nombre = nombre;		
	}

	public List<Long> getListaIdMateriasInscritas() {
		return listaIdMateriasInscritas;
	}

	public void setListaIdMateriasInscritas(List<Long> listaIdMateriasInscritas) {
		this.listaIdMateriasInscritas = listaIdMateriasInscritas;
	}

	public Carrera getCarrera() {
		return carrera;
	}

	public void setCarrera(Carrera carrera) {
		this.carrera = carrera;
	}

	public Short getSemestre() {
		return semestre;
	}

	public void setSemestre(Short semestre) {
		this.semestre = semestre;
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
				"\nNombre: " + nombre + 
				"\nCarrera: " + carrera + 
				"\nSemestre: " + semestre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(carrera, listaIdMateriasInscritas, nombre, semestre);
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
				&& Objects.equals(nombre, other.nombre) && carrera == other.carrera
				&& Objects.equals(semestre, other.semestre);
	}
}