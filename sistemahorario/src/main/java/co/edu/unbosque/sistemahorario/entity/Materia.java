package co.edu.unbosque.sistemahorario.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Un grupo de una asignatura del catalogo, con su horario y profesor
@Entity
@Table(name = "materia")
public class Materia {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;

	private int [] inicio;
	private int [] fin;

	@ManyToOne(optional = false)
	@JoinColumn(name = "asignatura_id", nullable = false)
	private Asignatura asignatura;

	@Column(nullable = false)
	private int grupo = 1;

	@ManyToOne
	@JoinColumn(name = "profesor_id")
	private Profesor profesorAsignado;

	@JsonIgnore
	@BatchSize(size = 100)
	@ManyToMany
	@JoinTable(name = "materia_estudiante",
			joinColumns = @JoinColumn(name = "materia_id"),
			inverseJoinColumns = @JoinColumn(name = "estudiante_id"))
	private List<Estudiante> listaEstudiantes = new ArrayList<>();

	public Materia() {
		super();
	}


	public Materia(int[] inicio, int[] fin, Asignatura asignatura, int grupo) {
		this.inicio = inicio;
		this.fin = fin;
		this.asignatura = asignatura;
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

	public Asignatura getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
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
				"\nAsignatura: " + (asignatura != null ? asignatura.getNombre() : null) +
				"\nGrupo: " + grupo;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fin);
		result = prime * result + Arrays.hashCode(inicio);
		result = prime * result + Objects.hash(grupo, id);
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
		return Arrays.equals(fin, other.fin) && id == other.id
				&& Arrays.equals(inicio, other.inicio) && grupo == other.grupo;
	}
}
