package co.edu.unbosque.sistemahorario.dto;

import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;

public class PlanEstudioDTO {

	private Carrera carrera;
	private short semestre;

	public PlanEstudioDTO() {
		super();
	}

	public PlanEstudioDTO(Carrera carrera, short semestre) {
		this.carrera = carrera;
		this.semestre = semestre;
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
		return carrera + " (" + semestre + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(carrera, semestre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanEstudioDTO other = (PlanEstudioDTO) obj;
		return carrera == other.carrera && semestre == other.semestre;
	}
}
