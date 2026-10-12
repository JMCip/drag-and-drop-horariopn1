package co.edu.unbosque.sistemahorario.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unbosque.sistemahorario.dto.EstudianteDTO;
import co.edu.unbosque.sistemahorario.repository.EstudianteRepository;

public class EstudianteService implements CRUDOPeration<EstudianteDTO>{

	@Autowired
	private EstudianteRepository estRepo;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public int create(EstudianteDTO data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<EstudianteDTO> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateById(Long id, EstudianteDTO data) {
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
