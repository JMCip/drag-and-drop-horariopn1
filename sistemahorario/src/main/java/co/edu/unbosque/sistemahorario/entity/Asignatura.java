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

/**
 * Una materia del <b>catálogo</b> (plan de estudios), tabla {@code asignatura}.
 * <p>
 * Cada asignatura existe una sola vez, aunque la vean varias carreras (por ejemplo, Matemáticas 1).
 * Aquí no hay horarios: los grupos con horario, profesor y estudiantes son {@link Materia}.
 * El catálogo se carga con el script {@code catalogo_materias.sql} y la aplicación solo lo consulta.
 */
// Materia del catalogo (plan de estudios). Los grupos con horario son Materia.
@Entity
@BatchSize(size = 100)
@Table(name = "asignatura")
public class Asignatura {

	/**
	 * Identificador autogenerado por la base de datos.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	/**
	 * Nombre de la asignatura. Es único en el catálogo y no puede ser nulo.
	 */
	@Column(nullable = false, unique = true)
	private String nombre;

	/**
	 * Planes de estudio en los que aparece: una fila por cada carrera y semestre.
	 * Es el lado inverso de la relación (la columna {@code asignatura_id} vive en {@link PlanEstudio}).
	 * {@code @BatchSize} agrupa las consultas de los planes para no hacer una por asignatura.
	 */
	@BatchSize(size = 100)
	@OneToMany(mappedBy = "asignatura")
	private List<PlanEstudio> planes = new ArrayList<>();

	public Asignatura() {
		super();
	}

	/**
	 * Crea una asignatura con ese nombre.
	 *
	 * @param nombre nombre de la asignatura
	 */
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
