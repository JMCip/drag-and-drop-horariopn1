package co.edu.unbosque.sistemahorario.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AsignaturaDTO {

	private long id;
	private String nombre;
	private List<PlanEstudioDTO> planes = new ArrayList<>();

	public AsignaturaDTO() {
		super();
	}

	public AsignaturaDTO(long id, String nombre, List<PlanEstudioDTO> planes) {
		this.id = id;
		this.nombre = nombre;
		this.planes = planes;
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

	public List<PlanEstudioDTO> getPlanes() {
		return planes;
	}

	public void setPlanes(List<PlanEstudioDTO> planes) {
		this.planes = planes;
	}

	@Override
	public String toString() {
		return "Asignatura " +
				"\nId: " + id +
				"\nNombre: " + nombre +
				"\nPlanes: " + planes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nombre, planes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AsignaturaDTO other = (AsignaturaDTO) obj;
		return id == other.id && Objects.equals(nombre, other.nombre) && Objects.equals(planes, other.planes);
	}
}
