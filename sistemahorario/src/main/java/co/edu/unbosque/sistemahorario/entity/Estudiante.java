package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Estudiante (tabla {@code estudiante}). Arma su horario inscribiéndose en grupos de materias.
 */
@Entity
@Table(name = "estudiante")
public class Estudiante extends Usuario{
	
	/**
	 * Materias en las que está inscrito. Es el lado inverso de la relación muchos a muchos:
	 * la tabla intermedia {@code materia_estudiante} la administra {@link Materia}.
	 * Se ignora al serializar a JSON para evitar referencias circulares.
	 */
	@JsonIgnore
	@ManyToMany(mappedBy = "listaEstudiantes")
	private List<Materia> listaMateriasInscritas = new ArrayList<>();
	
	/**
	 * Nombre completo del estudiante.
	 */
	private String nombre;
	
	/**
	 * Carrera que cursa. Se guarda como texto (por ejemplo {@code ING_SISTEMAS}) y no como número.
	 */
	@Enumerated(EnumType.STRING)
	private Carrera carrera;
	
	public Estudiante() {
		super();
	}
	
	public Estudiante(List<Materia> listaMateriasInscritas, String nombre) {
		super();
		this.listaMateriasInscritas = listaMateriasInscritas;
		this.nombre = nombre;
	}

	public Estudiante(String correo, String contrasenia) {
		super(correo, contrasenia);
	}
	
	public Estudiante(String correo, String contrasenia, List<Materia> listaMateriasInscritas, String nombre) {
		super(correo, contrasenia);
		this.listaMateriasInscritas = listaMateriasInscritas;
		this.nombre = nombre;		
	}
	
	public Estudiante(String correo, String contrasenia, String nombre) {
		super(correo, contrasenia);
		this.nombre = nombre;		
	}

	public List<Materia> getListaMateriasInscritas() {
		return listaMateriasInscritas;
	}

	public void setListaMateriasInscritas(List<Materia> listaMateriasInscritas) {
		this.listaMateriasInscritas = listaMateriasInscritas;
	}

	public Carrera getCarrera() {
		return carrera;
	}

	public void setCarrera(Carrera carrera) {
		this.carrera = carrera;
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
				"\nNombre: " + nombre + 
				"\nCarrera: " + carrera;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(carrera, nombre);
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
		return carrera == other.carrera && Objects.equals(nombre, other.nombre);
	}
}
