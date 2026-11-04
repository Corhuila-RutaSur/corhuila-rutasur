export interface ImagenExperiencia { id:string; ruta:string; orden:number; principal:boolean; activa?:boolean; url?:string; descripcion?:string; }
export interface ServicioPublico { id:string; tipo:string; nombre:string; descripcion?:string; }
export interface ActividadPublica { id:string; nombre:string; descripcion:string; duracionHoras:number; }
export interface SalidaPublica { id:string; fechaHoraInicio:string; cuposDisponibles:number; estado:string; }
export interface TipoExperiencia { id:string; nombre:string; descripcion?:string; activo:boolean; }
export interface Experiencia { id:string; nombre:string; destino:string; descripcion:string; imagenPrincipal?:string; calificacionPromedio?:number; imagenes?:ImagenExperiencia[]; servicios?:ServicioPublico[]; actividades?:ActividadPublica[]; salidas?:SalidaPublica[]; duracionHoras?:number; recomendaciones?:string; activa?:boolean; tipoExperienciaId?:string; }
export interface ExperienciaSolicitud { tipoExperienciaId:string; nombre:string; descripcion:string; destino:string; duracionHoras:number; recomendaciones?:string; }
