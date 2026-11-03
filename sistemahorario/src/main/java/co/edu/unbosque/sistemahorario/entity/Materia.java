package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.unbosque.sistemahorario.util.enums.Carrera;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Un <b>grupo</b> de una materia (tabla {@code materia}).
 * <p>
 * No representa la asignatura en abstracto sino un grupo concreto: la pareja
 * ({@code nombreMateria}, {@code grupo}) es única. Cada grupo tiene su propio horario,
 * su profesor y sus estudiantes inscritos.
 * <p>
 * El horario se guarda en {@link #inicio} y {@link #fin}: arreglos de 5 posiciones
 * (lunes a viernes) con la hora de entrada y de salida de cada día; el valor {@code -1}
 * indica que ese día no hay clase.
 */
@Entity
@Table(name = "materia")
public class Materia {

	/**
	 * Identificador autogenerado por la base de datos.
	 */
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	
	/**
	 * Hora de inicio de la clase por día, de lunes a viernes. {@code -1} = no hay clase ese día.
	 */
	private int [] inicio;
	/**
	 * Hora de fin de la clase por día, de lunes a viernes. {@code -1} = no hay clase ese día.
	 */
	private int [] fin;
	
	/**
	 * Nombre de la materia (igual para todos sus grupos).
	 */
	private String nombreMateria;
	
	/**
	 * Carrera a la que pertenece la materia.
	 */
	@Enumerated(EnumType.STRING)
	private Carrera carrera;
	
	/**
	 * Semestre en el que se ofrece (de 1 a 10).
	 */
	private short semestre;
	
	/**
	 * Número de grupo. Por defecto es 1 y no puede ser nulo.
	 */
	@Column(nullable = false)
	private int grupo = 1;
	
	/**
	 * Profesor que dicta el grupo. Puede ser {@code null} mientras no se asigne uno
	 * (la columna es {@code profesor_id}).
	 */
	@ManyToOne
	@JoinColumn(name = "profesor_id")
	private Profesor profesorAsignado;
	
	/**
	 * Estudiantes inscritos. Esta clase es la dueña de la relación muchos a muchos y
	 * administra la tabla intermedia {@code materia_estudiante}. Se ignora al serializar a JSON
	 * para evitar referencias circulares.
	 */
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "materia_estudiante",
			joinColumns = @JoinColumn(name = "materia_id"),
			inverseJoinColumns = @JoinColumn(name = "estudiante_id"))
	private List<Estudiante> listaEstudiantes = new ArrayList<>();
	
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
			Profesor profesorAsignado, List<Estudiante> listaEstudiantes) {
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

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	public Profesor getProfesorAsignado() {
		return profesorAsignado;
	}

	public void setProfesorAsignado(Profesor profesorAsignado) {
		this.profesorAsignado = profesorAsignado;
	}


	public List<Estudiante> getListaEstudiantes() {
		return listaEstudiantes;
	}

	public void setListaEstudiantes(List<Estudiante> listaEstudiantes) {
		this.listaEstudiantes = listaEstudiantes;
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
				"\nGrupo: " + grupo;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fin);
		result = prime * result + Arrays.hashCode(inicio);
		result = prime * result
				+ Objects.hash(carrera, grupo, id, nombreMateria, semestre);
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
				&& Arrays.equals(inicio, other.inicio) && Objects.equals(nombreMateria, other.nombreMateria)
				&& semestre == other.semestre && grupo == other.grupo;
	}
}