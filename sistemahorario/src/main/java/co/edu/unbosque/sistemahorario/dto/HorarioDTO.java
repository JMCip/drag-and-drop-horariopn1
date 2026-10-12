package co.edu.unbosque.sistemahorario.dto;

import java.util.Objects;

import co.edu.unbosque.sistemahorario.util.enums.Dia;

public class HorarioDTO {
	private long id;
	private int horaIncio;
	private int horaFin;
	private Dia dia;
	
	public HorarioDTO() {
		super();
	}

	public HorarioDTO(int horaIncio, int horaFin, Dia dia) {
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
		HorarioDTO other = (HorarioDTO) obj;
		return dia == other.dia && horaFin == other.horaFin && horaIncio == other.horaIncio && id == other.id;
	}
}
