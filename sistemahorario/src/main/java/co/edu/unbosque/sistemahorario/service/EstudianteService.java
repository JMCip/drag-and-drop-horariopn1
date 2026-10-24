package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unbosque.sistemahorario.dto.EstudianteDTO;
import co.edu.unbosque.sistemahorario.entity.Estudiante;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidEmailFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidNameFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidPasswordException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

public class EstudianteService implements CRUDOPeration<EstudianteDTO>{

	@Autowired
	private EstudianteRepository estRepo;

	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	private AdminRepository admRepo;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public int create(EstudianteDTO data) {
		try {
			LanzadorDeExcepcion.verificarEmail(data.getCorreo());
		} catch(InvalidEmailFormatException e) {
			System.out.println("Error de correo: " + e.getMessage());
			return 1;
		}
		
		try {
			LanzadorDeExcepcion.verificarContrasenia(data.getContrasenia());
		} catch(InvalidPasswordException e) {
			System.out.println("Error de contraseña: " + e.getMessage());
			return 2;
		}
		
		try {
			LanzadorDeExcepcion.verificarNombre(data.getNombre());
		} catch(InvalidNameFormatException e) {
			System.out.println("Error de nombre: " + e.getMessage());
			return 3;
		}
		
		if(profRepo.existByCorreo(data.getCorreo()) || estRepo.existByCorreo(data.getCorreo()) || admRepo.existByCorreo(data.getCorreo()))
			return 4;
		
		estRepo.save(mapper.map(data, Estudiante.class));
		return 0;
	}

	@Override
	public List<EstudianteDTO> getAll() {
		List<Estudiante> entityList = (List<Estudiante>) estRepo.findAll();
		List<EstudianteDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			EstudianteDTO dto = mapper.map(entity, EstudianteDTO.class);
			dtoList.add(dto);
		});
		
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<Estudiante> find = estRepo.findById(id);
		if(find.isPresent()) {
			estRepo.delete(find.get());
			return 0;
		}
		return 1;
	}

	@Override
	public int updateById(Long id, EstudianteDTO data) {
		Optional<Estudiante> find = estRepo.findById(id);
		
		try {
			LanzadorDeExcepcion.verificarEmail(data.getCorreo());
		} catch(InvalidEmailFormatException e) {
			System.out.println("Error de correo: " + e.getMessage());
			return 1;
		}
		
		try {
			LanzadorDeExcepcion.verificarContrasenia(data.getContrasenia());
		} catch(InvalidPasswordException e) {
			System.out.println("Error de contraseña: " + e.getMessage());
			return 2;
		}
		
		try {
			LanzadorDeExcepcion.verificarNombre(data.getNombre());
		} catch(InvalidNameFormatException e) {
			System.out.println("Error de nombre: " + e.getMessage());
			return 3;
		}
		
		if(profRepo.existByCorreo(data.getCorreo()) || admRepo.existByCorreo(data.getCorreo()))
			return 4;
		
		if(find.isPresent()) {
			Estudiante temp = find.get();
			
			if(estRepo.existByCorreo(data.getCorreo()) && temp.getCorreo() != data.getCorreo())
				return 5;
			
			temp.setNombre(data.getNombre());
			temp.setCorreo(data.getCorreo());
			temp.setContrasenia(data.getContrasenia());
			estRepo.save(temp);
			return 0;
		}
		
		return 6;
	}

	@Override
	public long count() {
		return estRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return estRepo.existsById(id);
	}
	
	public int validarInicioSesion(String correo, String pw) {
	    try {
	        LanzadorDeExcepcion.verificarEmail(correo);
	    } catch (InvalidEmailFormatException e) {
	        System.out.println("Error de correo: " + e.getMessage());
	        return 1;
	    }

	    List<Estudiante> entityList = (List<Estudiante>) estRepo.findAll();
	    for (Estudiante user : entityList) {
	        if (user.getCorreo().equalsIgnoreCase(correo) && user.getContrasenia().equals(pw)) {
	            return 0;
	        }
	    }
	    return 2;
	}
}
