export type TipoServicio='TRANSPORTE'|'ALOJAMIENTO'|'ACTIVIDAD'|'ALIMENTACION';
export interface ServicioExperiencia{id:number;experienciaId:number;tipo:TipoServicio;nombre:string;descripcion?:string;activo:boolean;}
export interface SalidaExperiencia{id:number;experienciaId:number;fechaHoraInicio:string;fechaHoraFin?:string;cuposTotales:number;cuposDisponibles:number;estado:string;observaciones?:string;}
export interface ProgramacionServicio{id:number;salidaExperienciaId:number;servicioExperienciaId:number;precioUnitario:number;cuposTotales?:number;cuposDisponibles?:number;activo:boolean;}
