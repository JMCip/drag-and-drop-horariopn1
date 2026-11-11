package co.edu.unbosque.sistemahorario.service;

import java.util.List;

/**
 * Contrato común de las operaciones CRUD de los servicios.
 * <p>
 * Las operaciones que pueden fallar devuelven un <b>código entero</b> en lugar de lanzar
 * excepciones: {@code 0} significa éxito y cualquier otro valor identifica un error concreto
 * (cada implementación documenta sus códigos). El controlador traduce ese código a un estado HTTP.
 *
 * @param <T> tipo de DTO que maneja el servicio
 */
public interface CRUDOPeration<T> {
	
	/**
	 * Crea un registro nuevo.
	 *
	 * @param data datos del registro a crear
	 * @return {@code 0} si se creó; otro valor según el error (ver la implementación)
	 */
	public int create(T data);
	/**
	 * Obtiene todos los registros.
	 *
	 * @return lista con todos los registros convertidos a DTO
	 */
	public List<T> getAll();
	/**
	 * Elimina un registro por su identificador.
	 *
	 * @param id identificador del registro
	 * @return {@code 0} si se eliminó; {@code 1} si no existe
	 */
	public int deleteById(Long id);
	/**
	 * Actualiza un registro existente.
	 *
	 * @param id   identificador del registro a modificar
	 * @param data nuevos datos
	 * @return {@code 0} si se actualizó; otro valor según el error (ver la implementación)
	 */
	public int updateById(Long id, T data);
	/**
	 * Cuenta los registros existentes.
	 *
	 * @return cantidad de registros
	 */
	public long count();
	/**
	 * Indica si existe un registro con ese identificador.
	 *
	 * @param id identificador a buscar
	 * @return {@code true} si existe
	 */
	public boolean exist(Long id);
}
