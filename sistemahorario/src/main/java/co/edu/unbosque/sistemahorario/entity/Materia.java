package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
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
	
	@JsonIgnore
	@OneToMany(mappedBy = "horario_id", cascade = CascadeType.ALL)
	private ArrayList<Horario> horario;
	
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

	public Materia(ArrayList<Horario> horario, String nombreMateria, Carrera carrera, short semestre,
			Profesor profesorAsignado, ArrayList<Estudiante> listaEstudiantes) {
		this.horario = horario;
		this.nombreMateria = nombreMateria;
		this.carrera = carrera;
		this.semestre = semestre;
		this.profesorAsignado = profesorAsignado;
		this.listaEstudiantes = listaEstudiantes;
	}

	public Materia(ArrayList<Horario> horario, String nombreMateria, Carrera carrera, short semestre) {
		this.horario = horario;
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

	public ArrayList<Horario> getHorario() {
		return horario;
	}

	public void setHorario(ArrayList<Horario> horario) {
		this.horario = horario;
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
				"\nHorario: " + horario + 
				"\nNombre materia:" + nombreMateria + 
				"\nCarrera: " + carrera + 
				"\nSemestre: " + semestre + 
				"\nProfesor asignado: " + profesorAsignado + 
				"\nLista estudiantes: " + listaEstudiantes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carrera, horario, id, listaEstudiantes, nombreMateria, profesorAsignado, semestre);
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
		return carrera == other.carrera && Objects.equals(horario, other.horario) && id == other.id
				&& Objects.equals(listaEstudiantes, other.listaEstudiantes)
				&& Objects.equals(nombreMateria, other.nombreMateria)
				&& Objects.equals(profesorAsignado, other.profesorAsignado) && semestre == other.semestre;
	}
}