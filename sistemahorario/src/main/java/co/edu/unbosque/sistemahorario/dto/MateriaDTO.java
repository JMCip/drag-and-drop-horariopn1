package co.edu.unbosque.sistemahorario.dto;

import java.util.ArrayList;
import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;

public class MateriaDTO {

	private long id;
	private ArrayList<Long> idHorarios;
	
	private String nombreMateria;
	private Carrera carrera;
	private short semestre;
	private Long idProfesorAsignado;
	private ArrayList<Long> listaIdEstudiantes;
	
	
	public MateriaDTO() {
		super();
	}

	public MateriaDTO(ArrayList<Long> idHorarios, String nombreMateria, Carrera carrera, short semestre,
			Long idProfesorAsignado, ArrayList<Long> listaIdEstudiantes) {
		this.idHorarios = idHorarios;
		this.nombreMateria = nombreMateria;
		this.carrera = carrera;
		this.semestre = semestre;
		this.idProfesorAsignado = idProfesorAsignado;
		this.listaIdEstudiantes = listaIdEstudiantes;
	}

	public MateriaDTO(ArrayList<Long> idHorarios, String nombreMateria, Carrera carrera, short semestre) {
		this.idHorarios = idHorarios;
		this.nombreMateria = nombreMateria;
		this.carrera = carrera;
		this.semestre = semestre;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ArrayList<Long> getIdHorarios() {
		return idHorarios;
	}

	public void setIdHorarios(ArrayList<Long> idHorarios) {
		this.idHorarios = idHorarios;
	}

	public String getNombreMateria() {
		return nombreMateria;
	}

	public void setNombreMateria(String nombreMateria) {
		this.nombreMateria = nombreMateria;
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

	public Long getIdProfesorAsignado() {
		return idProfesorAsignado;
	}

	public void setIdProfesorAsignado(Long idProfesorAsignado) {
		this.idProfesorAsignado = idProfesorAsignado;
	}

	public ArrayList<Long> getListaIdEstudiantes() {
		return listaIdEstudiantes;
	}

	public void setListaIdEstudiantes(ArrayList<Long> listaIdEstudiantes) {
		this.listaIdEstudiantes = listaIdEstudiantes;
	}

	@Override
	public String toString() {
		return "MateriaDTO " + 
				"\nId: " + id + 
				"\nId horarios: " + idHorarios + 
				"\nNombre materia:" + nombreMateria + 
				"\nCarrera: " + carrera + 
				"\nSemestre: " + semestre + 
				"\nId profesor asignado: " + idProfesorAsignado + 
				"\nLista id estudiantes: " + listaIdEstudiantes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carrera, id, idHorarios, idProfesorAsignado, listaIdEstudiantes, nombreMateria, semestre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MateriaDTO other = (MateriaDTO) obj;
		return carrera == other.carrera && id == other.id && Objects.equals(idHorarios, other.idHorarios)
				&& Objects.equals(idProfesorAsignado, other.idProfesorAsignado)
				&& Objects.equals(listaIdEstudiantes, other.listaIdEstudiantes)
				&& Objects.equals(nombreMateria, other.nombreMateria) && semestre == other.semestre;
	}
}