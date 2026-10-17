export type TipoTraslape = 'NINGUNO' | 'PARCIAL' | 'TOTAL';

export interface RespuestaValidacionModel {
  tipoTraslape: TipoTraslape;
  seccionConflictoId?: number;
  materiaConflicto?: string;
  detalleFranja?: string;
  mensaje?: string;
}
