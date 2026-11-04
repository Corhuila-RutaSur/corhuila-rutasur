export interface OpcionServicioSalida { programacionId:string; servicioIdExterno:string; nombre:string; precio:number; cuposDisponibles:number; activo:boolean; }
export interface OpcionActividadSalida { programacionId:string; actividadIdExterna:string; nombre:string; precio:number; activo:boolean; }
export interface DisponibilidadSalida { salidaId:string; experienciaId:string; fechaHoraInicio:string; cuposDisponibles:number; estado:string; servicios:OpcionServicioSalida[]; actividades:OpcionActividadSalida[]; }
