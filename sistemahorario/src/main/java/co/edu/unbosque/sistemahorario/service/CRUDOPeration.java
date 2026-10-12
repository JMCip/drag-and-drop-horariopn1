package co.edu.unbosque.sistemahorario.service;

import java.util.List;

public interface CRUDOPeration<T> {
	
	public int create(T data);
	public List<T> getAll();
	public int deleteById(Long id);
	public int updateById(Long id, T data);
	public long count();
	public boolean exist(Long id);
}
