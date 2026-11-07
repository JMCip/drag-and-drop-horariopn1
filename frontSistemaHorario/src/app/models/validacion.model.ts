/**
 * Resultado de comparar un horario candidato con los ya ubicados:
 * `NINGUNO` (no choca), `PARCIAL` (se solapan algunas horas) o `TOTAL` (mismo bloque exacto).
 */
export type TipoTraslape = 'NINGUNO' | 'PARCIAL' | 'TOTAL';

/**
 * Resultado de validar un cruce de horario, con los datos de la materia con la que choca (si hay conflicto).
 */
export interface RespuestaValidacionModel {
  tipoTraslape: TipoTraslape;
  seccionConflictoId?: number;
  materiaConflicto?: string;
  detalleFranja?: string;
  mensaje?: string;
}
