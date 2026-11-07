package co.edu.unbosque.sistemahorario.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;

/**
 * Objeto de transferencia de una materia (un grupo concreto).
 * <p>
 * El profesor y los estudiantes se envían solo como ids ({@code idProfesorAsignado} y
 * {@code listaIdEstudiantes}). El horario va en {@code inicio} y {@code fin}: 5 valores,
 * de lunes a viernes, con {@code -1} cuando ese día no hay clase.
 */
public class MateriaDTO {

	private long id;
	private int [] inicio;
	private int [] fin;
	private long idAsignatura;
	private String nombreMateria;
	private int grupo = 1;
	private Long idProfesorAsignado;
	private List<Long> listaIdEstudiantes = new ArrayList<>();
	private List<PlanEstudioDTO> planes = new ArrayList<>();
	// Solo en las respuestas para un estudiante: su carrera y el semestre de la materia en ella
	private Carrera carrera;
	private Short semestre;

	public MateriaDTO() {
		super();
	}


	public MateriaDTO(int[] inicio, int[] fin, long idAsignatura, int grupo) {
		this.inicio = inicio;
		this.fin = fin;
		this.idAsignatura = idAsignatura;
		this.grupo = grupo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int[] getInicio() {
		return inicio;
	}

	public void setInicio(int[] inicio) {
		this.inicio = inicio;
	}

	public int[] getFin() {
		return fin;
	}

	public void setFin(int[] fin) {
		this.fin = fin;
	}

	public long getIdAsignatura() {
		return idAsignatura;
	}

	public void setIdAsignatura(long idAsignatura) {
		this.idAsignatura = idAsignatura;
	}

	public String getNombreMateria() {
		return nombreMateria;
	}

	public void setNombreMateria(String nombreMateria) {
		this.nombreMateria = nombreMateria;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	public Long getIdProfesorAsignado() {
		return idProfesorAsignado;
	}

	public void setIdProfesorAsignado(Long idProfesorAsignado) {
		this.idProfesorAsignado = idProfesorAsignado;
	}

	public List<Long> getListaIdEstudiantes() {
		return listaIdEstudiantes;
	}

	public void setListaIdEstudiantes(List<Long> listaIdEstudiantes) {
		this.listaIdEstudiantes = listaIdEstudiantes;
	}

	public List<PlanEstudioDTO> getPlanes() {
		return planes;
	}

	public void setPlanes(List<PlanEstudioDTO> planes) {
		this.planes = planes;
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

	@Override
	public String toString() {
		return "Materia " +
				"\nId: " + id +
				"\nInicio: " + Arrays.toString(inicio) +
				"\nFin: " + Arrays.toString(fin) +
				"\nId asignatura: " + idAsignatura +
				"\nNombre materia:" + nombreMateria +
				"\nGrupo: " + grupo +
				"\nId profesor asignado: " + idProfesorAsignado +
				"\nLista id estudiantes: " + listaIdEstudiantes +
				"\nPlanes: " + planes;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fin);
		result = prime * result + Arrays.hashCode(inicio);
		result = prime * result
				+ Objects.hash(carrera, grupo, id, idAsignatura, idProfesorAsignado, listaIdEstudiantes, nombreMateria, planes, semestre);
		return result;
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
		return carrera == other.carrera && Arrays.equals(fin, other.fin) && id == other.id
				&& idAsignatura == other.idAsignatura && Objects.equals(idProfesorAsignado, other.idProfesorAsignado)
				&& Arrays.equals(inicio, other.inicio) && Objects.equals(listaIdEstudiantes, other.listaIdEstudiantes)
				&& Objects.equals(nombreMateria, other.nombreMateria) && Objects.equals(planes, other.planes)
				&& Objects.equals(semestre, other.semestre) && grupo == other.grupo;
	}
}
