package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representación JPA privada de una identidad.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usuario")
class UsuarioEntidad {
    @Id
    private UUID id;
    @Column(name = "tipo_documento")
    private String tipoDocumento;
    @Column(name = "numero_documento")
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    @Column(name = "password_hash")
    private String passwordHash;
    private boolean activo;
    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}
