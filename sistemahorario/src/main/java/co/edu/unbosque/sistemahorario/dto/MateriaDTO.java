package co.edu.unbosque.sistemahorario.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;

public class MateriaDTO {

	private long id;
	private int [] inicio;
	private int [] fin;
	private String nombreMateria;
	private Carrera carrera;
	private short semestre;
	private int grupo = 1;
	private Long idProfesorAsignado;
	private List<Long> listaIdEstudiantes = new ArrayList<>();
	
	public MateriaDTO() {
		super();
	}


	public MateriaDTO(int[] inicio, int[] fin, String nombreMateria, Carrera carrera, short semestre) {
		this.inicio = inicio;
		this.fin = fin;
		this.nombreMateria = nombreMateria;
		this.carrera = carrera;
		this.semestre = semestre;
	}

	public MateriaDTO(int[] inicio, int[] fin, String nombreMateria, Carrera carrera, short semestre,
			Long idProfesorAsignado, List<Long> listaIdEstudiantes) {
		this.inicio = inicio;
		this.fin = fin;
		this.nombreMateria = nombreMateria;
		this.carrera = carrera;
		this.semestre = semestre;
		this.idProfesorAsignado = idProfesorAsignado;
		this.listaIdEstudiantes = listaIdEstudiantes;
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

	@Override
	public String toString() {
		return "Materia " + 
				"\nId: " + id + 
				"\nInicio: " + Arrays.toString(inicio) + 
				"\nFin: " + Arrays.toString(fin) +	
				"\nNombre materia:" + nombreMateria + 
				"\nCarrera: " + carrera + 
				"\nSemestre: " + semestre + 
				"\nGrupo: " + grupo + 
				"\nId profesor asignado: " + idProfesorAsignado + 
				"\nLista id estudiantes: " + listaIdEstudiantes;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fin);
		result = prime * result + Arrays.hashCode(inicio);
		result = prime * result
				+ Objects.hash(carrera, grupo, id, idProfesorAsignado, listaIdEstudiantes, nombreMateria, semestre);
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
				&& Objects.equals(idProfesorAsignado, other.idProfesorAsignado) && Arrays.equals(inicio, other.inicio)
				&& Objects.equals(listaIdEstudiantes, other.listaIdEstudiantes)
				&& Objects.equals(nombreMateria, other.nombreMateria) && semestre == other.semestre
				&& grupo == other.grupo;
	}
}