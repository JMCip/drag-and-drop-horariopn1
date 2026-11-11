import { Carrera } from './carrera.enum';
import { Role } from './role.enum';

/**
 * Datos comunes de un usuario. La contraseña es opcional porque el back no la devuelve en claro
 * y, al editar, si se omite se conserva la actual.
 */
export interface UsuarioDTO {
  id?: number;
  correo: string;
  contrasenia?: string;
}


/**
 * Administrador.
 */
export interface AdminDTO extends UsuarioDTO {}


/**
 * Estudiante, con su carrera, su semestre y los ids de las materias en las que está inscrito.
 */
export interface EstudianteDTO extends UsuarioDTO {
  nombre: string;
  carrera?: Carrera | null;
  /**
   * Semestre que cursa el estudiante (de 1 a 12).
   */
  semestre?: number | null;
  listaIdMateriasInscritas: number[];
}


/**
 * Profesor, con los ids de las materias que dicta.
 */
export interface ProfesorDTO extends UsuarioDTO {
  nombre: string;
  listaIdMaterias: number[];
}


/**
 * Datos del usuario que tiene la sesión iniciada.
 */
export interface UsuarioSesionModel {
  id?: number;
  nombre: string;
  correo: string;
  rol: Role;
}
