package co.edu.unbosque.sistemahorario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.sistemahorario.dto.AdminDTO;
import co.edu.unbosque.sistemahorario.entity.Admin;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidEmailFormatException;
import co.edu.unbosque.sistemahorario.util.exceptions.InvalidPasswordException;
import co.edu.unbosque.sistemahorario.util.exceptions.LanzadorDeExcepcion;

@Service
public class AdminService implements CRUDOPeration<AdminDTO>{
	
	@Autowired
	private AdminRepository admRepo;
	
	@Autowired
	private ProfesorRepository profRepo;
	
	@Autowired
	
	private EstudianteRepository estRepo;
	@Autowired
	private ModelMapper mapper;

	@Override
	public int create(AdminDTO data) {
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
		
		if(profRepo.existByCorreo(data.getCorreo()) || estRepo.existByCorreo(data.getCorreo()) || admRepo.existByCorreo(data.getCorreo()))
			return 3;
		
		admRepo.save(mapper.map(data, Admin.class));
		return 0;
	}

	@Override
	public List<AdminDTO> getAll() {
		List<Admin> entityList = (List<Admin>) admRepo.findAll();
		List<AdminDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			AdminDTO dto = mapper.map(entity, AdminDTO.class);
			dtoList.add(dto);
		});
		
		return dtoList;
	}

	@Override
	public int deleteById(Long id) {
		Optional<Admin> find = admRepo.findById(id);
		if(find.isPresent()) {
			admRepo.delete(find.get());
			return 0;
		}
		return 1;
	}

	@Override
	public int updateById(Long id, AdminDTO data) {
		Optional<Admin> find = admRepo.findById(id);
		
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
		
		if(profRepo.existByCorreo(data.getCorreo()) || estRepo.existByCorreo(data.getCorreo()))
			return 3;
		
		if(find.isPresent()) {
			Admin temp = find.get();
			
			if(admRepo.existByCorreo(data.getCorreo()) && temp.getCorreo() != data.getCorreo())
				return 5;
			
			temp.setCorreo(data.getCorreo());
			temp.setContrasenia(data.getContrasenia());
			admRepo.save(temp);
			return 0;
		}
		return 4;
	}

	@Override
	public long count() {
		return admRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return admRepo.existsById(id);
	}
	
	public int validarInicioSesion(String correo, String pw) {
	    try {
	        LanzadorDeExcepcion.verificarEmail(correo);
	    } catch (InvalidEmailFormatException e) {
	        System.out.println("Error de correo: " + e.getMessage());
	        return 1;
	    }

	    List<Admin> entityList = (List<Admin>) admRepo.findAll();
	    for (Admin user : entityList) {
	        if (user.getCorreo().equalsIgnoreCase(correo) && user.getContrasenia().equals(pw)) {
	            return 0;
	        }
	    }
	    return 2;
	}
}
