package co.edu.unbosque.sistemahorario.entity;

import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// En que semestre va una asignatura dentro de una carrera
@Entity
@Table(name = "plan_estudio")
public class PlanEstudio {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "asignatura_id", nullable = false)
	private Asignatura asignatura;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Carrera carrera;

	@Column(nullable = false)
	private short semestre;

	public PlanEstudio() {
		super();
	}

	public PlanEstudio(Asignatura asignatura, Carrera carrera, short semestre) {
		this.asignatura = asignatura;
		this.carrera = carrera;
		this.semestre = semestre;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Asignatura getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	public Carrera getCarrera() {
		return carrera;
	}

	public void setCarrera(Carrera carrera) {
		this.carrera = carrera;
	}

	public short getSemestre() {
		return semestre;
	}

	public void setSemestre(short semestre) {
		this.semestre = semestre;
	}

	@Override
	public String toString() {
		return "Plan de estudio " +
				"\nId: " + id +
				"\nCarrera: " + carrera +
				"\nSemestre: " + semestre;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carrera, id, semestre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanEstudio other = (PlanEstudio) obj;
		return carrera == other.carrera && id == other.id && semestre == other.semestre;
	}
}
