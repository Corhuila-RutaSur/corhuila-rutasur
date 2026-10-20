/** Datos de una persona que asistirá a una reserva. */
export interface ParticipanteReservaSolicitud {
  tipoDocumento: string;
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  correo: string;
  telefono: string;
  responsable: boolean;
}

/** Contrato para crear una reserva desde el catálogo público autenticado. */
export interface ReservaCreacionSolicitud {
  usuarioId: number;
  salidaExperienciaId: number;
  programacionesServicioIds: number[];
  participantes: ParticipanteReservaSolicitud[];
  observaciones?: string;
}

/** Información esencial de una reserva confirmada. */
export interface ReservaCreada {
  id: number;
  codigo: string;
  estado: string;
  total: number;
}

/** Resumen de una reserva que se muestra al usuario autenticado. */
export interface Reserva {
  id: number;
  codigo: string;
  usuarioId: number;
  salidaExperienciaId: number;
  estado: 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'FINALIZADA' | string;
  subtotal: number;
  descuento: number;
  total: number;
  fechaReserva: string;
  observaciones?: string;
  experienciaId?: number;
  experienciaNombre?: string;
  experienciaDestino?: string;
}
