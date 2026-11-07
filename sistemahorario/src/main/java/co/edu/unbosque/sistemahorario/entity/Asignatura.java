package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Materia del catalogo (plan de estudios). Los grupos con horario son Materia.
@Entity
@BatchSize(size = 100)
@Table(name = "asignatura")
public class Asignatura {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	@Column(nullable = false, unique = true)
	private String nombre;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "asignatura")
	private List<PlanEstudio> planes = new ArrayList<>();

	public Asignatura() {
		super();
	}

	public Asignatura(String nombre) {
		this.nombre = nombre;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<PlanEstudio> getPlanes() {
		return planes;
	}

	public void setPlanes(List<PlanEstudio> planes) {
		this.planes = planes;
	}

	@Override
	public String toString() {
		return "Asignatura " +
				"\nId: " + id +
				"\nNombre: " + nombre;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Asignatura other = (Asignatura) obj;
		return id == other.id && Objects.equals(nombre, other.nombre);
	}
}
