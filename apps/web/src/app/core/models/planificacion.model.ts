/** Tipos comerciales que Catálogo puede ofrecer dentro de una experiencia. */
export type TipoServicio = 'TRANSPORTE' | 'ALOJAMIENTO' | 'ACTIVIDAD' | 'ALIMENTACION';

/** Servicio propio de una experiencia, administrado por Catálogo. */
export interface ServicioExperiencia { id:string; experienciaId:string; tipo:TipoServicio; nombre:string; descripcion?:string; activo:boolean; }
/** Salida y cupos, administrados por Reservas. */
export interface SalidaExperiencia { id:string; experienciaIdExterna:string; experienciaNombre:string; fechaHoraInicio:string; cuposTotales:number; cuposDisponibles:number; estado:string; }
/** Servicio comercial habilitado para una salida concreta. */
export interface ProgramacionServicio { id:string; salidaId:string; servicioIdExterno:string; nombre:string; precio:number; cuposTotales:number; cuposDisponibles:number; activo:boolean; }
export interface SalidaCreacionSolicitud { experienciaIdExterna:string; experienciaNombre:string; fechaHoraInicio:string; cuposTotales:number; }
export interface ProgramacionServicioSolicitud { servicioIdExterno:string; nombre:string; precio:number; cuposTotales:number; }
