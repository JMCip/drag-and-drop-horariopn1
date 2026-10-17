import { Role } from './role.enum';

export interface UsuarioDTO {
  id?: number;
  correo: string;
  contrasenia?: string;
}


export interface AdminDTO extends UsuarioDTO {}


export interface EstudianteDTO extends UsuarioDTO {
  nombre: string;
  listaIdMateriasInscritas: number[];
}


export interface ProfesorDTO extends UsuarioDTO {
  nombre: string;
  listaIdMaterias: number[];
}


export interface UsuarioSesionModel {
  id?: number;
  nombre: string;
  correo: string;
  rol: Role;
}
