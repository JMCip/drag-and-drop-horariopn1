package co.edu.unbosque.sistemahorario.entity;

import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Dia;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "horario")
public class Horario {
	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
	private int horaIncio;
	private int horaFin;
	private Dia dia;
	
	public Horario() {
		super();
	}

	public Horario(int horaIncio, int horaFin, Dia dia) {
		this.horaIncio = horaIncio;
		this.horaFin = horaFin;
		this.dia = dia;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getHoraIncio() {
		return horaIncio;
	}

	public void setHoraIncio(int horaIncio) {
		this.horaIncio = horaIncio;
	}

	public int getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(int horaFin) {
		this.horaFin = horaFin;
	}

	public Dia getDia() {
		return dia;
	}

	public void setDia(Dia dia) {
		this.dia = dia;
	}

	@Override
	public String toString() {
		return "Horario " + 
				"\nId: " + id + 
				"\nHora incio: " + horaIncio + 
				"\nHora fin: " + horaFin + 
				"\nDia: " + dia;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dia, horaFin, horaIncio, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Horario other = (Horario) obj;
		return dia == other.dia && horaFin == other.horaFin && horaIncio == other.horaIncio && id == other.id;
	}
}