export enum Carrera {
  ING_SISTEMAS = 'ING_SISTEMAS',
  ING_INDUSTRIAL = 'ING_INDUSTRIAL',
  BIOING = 'BIOING',
  ING_ROBOTICA = 'ING_ROBOTICA',
  MEDICINA = 'MEDICINA',
  PSICOLOGIA = 'PSICOLOGIA',
  MAT_CIENCIA_DATOS = 'MAT_CIENCIA_DATOS',
  ESTADISTICA = 'ESTADISTICA',
  FISICA = 'FISICA'
}

export const ETIQUETAS_CARRERA: Record<Carrera, string> = {
  [Carrera.ING_SISTEMAS]: 'Ingeniería de Sistemas',
  [Carrera.ING_INDUSTRIAL]: 'Ingeniería Industrial',
  [Carrera.BIOING]: 'Bioingeniería',
  [Carrera.ING_ROBOTICA]: 'Ingeniería Robótica',
  [Carrera.MEDICINA]: 'Medicina',
  [Carrera.PSICOLOGIA]: 'Psicología',
  [Carrera.MAT_CIENCIA_DATOS]: 'Matemática y Ciencia de Datos',
  [Carrera.ESTADISTICA]: 'Estadística',
  [Carrera.FISICA]: 'Física'
};
