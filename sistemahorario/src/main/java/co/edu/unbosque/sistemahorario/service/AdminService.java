package co.edu.unbosque.sistemahorario.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unbosque.sistemahorario.dto.AdminDTO;
import co.edu.unbosque.sistemahorario.repository.AdminRepository;

public class AdminService implements CRUDOPeration<AdminDTO>{

	@Autowired
	private AdminRepository admRepo;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public int create(AdminDTO data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<AdminDTO> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateById(Long id, AdminDTO data) {
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
