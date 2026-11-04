/** Datos de una persona que asistirá a una reserva. */
export interface ParticipanteReservaSolicitud { tipoDocumento:string; numeroDocumento:string; nombres:string; apellidos:string; responsable?:boolean; correo?:string; telefono?:string; }
export interface ReservaCreacionSolicitud { usuarioIdExterno:string; salidaId:string; cantidadParticipantes:number; participantes:ParticipanteReservaSolicitud[]; programacionesServicio:string[]; programacionesActividad:string[]; }
export interface ReservaCreada { id:string; codigo:string; salidaId:string; cantidadParticipantes:number; total:number; estado:string; }
export interface Reserva { id:string; codigo:string; salidaId:string; experienciaId?:string; experienciaNombre:string; fechaHoraInicio?:string; cantidadParticipantes:number; total:number; estado:string; creadaEn:string; fechaReserva?:string; experienciaDestino?:string; salidaExperienciaId?:string; }
