package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unbosque.sistemahorario.dto.ProfesorDTO;
import co.edu.unbosque.sistemahorario.entity.Profesor;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidEmailFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidNameFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidPasswordException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

public class ProfesorService implements CRUDOPeration<ProfesorDTO>{

	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	private EstudianteRepository estRepo;
	
	@Autowired
	private AdminRepository admRepo;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public int create(ProfesorDTO data) {
		
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
		
		profRepo.save(mapper.map(data, Profesor.class));
		return 0;
	}

	@Override
	public List<ProfesorDTO> getAll() {
		List<Profesor> entityList = (List<Profesor>) profRepo.findAll();
		List<ProfesorDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			ProfesorDTO dto = mapper.map(entity,  ProfesorDTO.class);
			dtoList.add(dto);
		});
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<Profesor> find = profRepo.findById(id);
		
		if(find.isPresent()) {
			profRepo.delete(find.get());
			return 0;
		}
		
		return 1;
	}

	@Override
	public int updateById(Long id, ProfesorDTO data) {
		Optional<Profesor> find = profRepo.findById(id);

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
		
		if(estRepo.existByCorreo(data.getCorreo()) || admRepo.existByCorreo(data.getCorreo()))
			return 4;
		
		if(find.isPresent()) {
			Profesor temp = find.get();
			
			if(profRepo.existByCorreo(data.getCorreo()) && temp.getCorreo() != data.getCorreo())
				return 5;
			
			temp.setNombre(data.getNombre());
			temp.setCorreo(data.getCorreo());
			temp.setContrasenia(data.getContrasenia());
			profRepo.save(temp);
			return 0;
		}
		
		return 6;
	}

	@Override
	public long count() {
		return profRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return profRepo.existsById(id) ? true : false;
	}
}
