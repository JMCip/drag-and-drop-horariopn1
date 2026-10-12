package co.edu.unbosque.sistemahorario.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unbosque.sistemahorario.dto.ProfesorDTO;
import co.edu.unbosque.sistemahorario.repository.ProfesorRepository;

public class ProfesorService implements CRUDOPeration<ProfesorDTO>{

	@Autowired
	private ProfesorRepository admRepo;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public int create(ProfesorDTO data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ProfesorDTO> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateById(Long id, ProfesorDTO data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean exist(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
