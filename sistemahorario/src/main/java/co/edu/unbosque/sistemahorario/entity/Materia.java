package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "materia")
public class Materia {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	
	private int [] inicio;
	private int [] fin;
	
	private String nombreMateria;
	
	@Enumerated(EnumType.STRING)
	private Carrera carrera;
	
	private short semestre;
	
	@ManyToOne
	@JoinColumn(name = "profesor")
	private Profesor profesorAsignado;
	
	@JsonIgnore
	@OneToMany(mappedBy = "estudiante_id", cascade = CascadeType.ALL)
	private ArrayList<Estudiante> listaEstudiantes;
	
	public Materia() {
		super();
	}


	public Materia(int[] inicio, int[] fin, String nombreMateria, Carrera carrera, short semestre) {
		this.inicio = inicio;
		this.fin = fin;
		this.nombreMateria = nombreMateria;
		this.carrera = carrera;
		this.semestre = semestre;
	}

	public Materia(int[] inicio, int[] fin, String nombreMateria, Carrera carrera, short semestre,
			Profesor profesorAsignado, ArrayList<Estudiante> listaEstudiantes) {
		this.inicio = inicio;
		this.fin = fin;
		this.nombreMateria = nombreMateria;
		this.carrera = carrera;
		this.semestre = semestre;
		this.profesorAsignado = profesorAsignado;
		this.listaEstudiantes = listaEstudiantes;
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

	public Profesor getProfesorAsignado() {
		return profesorAsignado;
	}

	public void setProfesorAsignado(Profesor profesorAsignado) {
		this.profesorAsignado = profesorAsignado;
	}


	public ArrayList<Estudiante> getListaEstudiantes() {
		return listaEstudiantes;
	}

	public void setListaEstudiantes(ArrayList<Estudiante> listaEstudiantes) {
		this.listaEstudiantes = listaEstudiantes;
	}

	@Override
	public String toString() {
		return "Materia " + 
				"\nId: " + id + 
				"\nInicio: " + inicio + 
				"\nFin: " + fin +	
				"\nNombre materia:" + nombreMateria + 
				"\nCarrera: " + carrera + 
				"\nSemestre: " + semestre + 
				"\nProfesor asignado: " + profesorAsignado + 
				"\nLista estudiantes: " + listaEstudiantes;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fin);
		result = prime * result + Arrays.hashCode(inicio);
		result = prime * result
				+ Objects.hash(carrera, id, listaEstudiantes, nombreMateria, profesorAsignado, semestre);
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
		Materia other = (Materia) obj;
		return carrera == other.carrera && Arrays.equals(fin, other.fin) && id == other.id
				&& Arrays.equals(inicio, other.inicio) && Objects.equals(listaEstudiantes, other.listaEstudiantes)
				&& Objects.equals(nombreMateria, other.nombreMateria)
				&& Objects.equals(profesorAsignado, other.profesorAsignado) && semestre == other.semestre;
	}
}