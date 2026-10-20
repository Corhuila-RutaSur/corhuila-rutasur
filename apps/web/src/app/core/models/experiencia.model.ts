export interface ImagenExperiencia { id:number; url:string; descripcion?:string; orden:number; principal:boolean; }
export interface Experiencia { id:number; tipoExperienciaId:number; creadoPorUsuarioId:number; nombre:string; descripcion:string; destino:string; duracionHoras?:number; recomendaciones?:string; activo:boolean; imagenes:ImagenExperiencia[]; }
export interface ImagenExperienciaSolicitud { url:string; descripcion?:string; orden:number; principal:boolean; }
export interface ExperienciaSolicitud { tipoExperienciaId:number; creadoPorUsuarioId:number; nombre:string; descripcion:string; destino:string; duracionHoras?:number; recomendaciones?:string; imagenes:ImagenExperienciaSolicitud[]; }
