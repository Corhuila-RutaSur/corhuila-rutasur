package co.edu.corhuila.rutasur.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Asigna un rol funcional a una cuenta de usuario.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "usuario_tipo")
public class UsuarioTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_usuario_id", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(name = "asignado_en", nullable = false)
    private OffsetDateTime asignadoEn;

    /**
     * Crea una asignacion de rol para una cuenta recien registrada.
     *
     * @param usuario     cuenta a la que se asigna el rol.
     * @param tipoUsuario rol funcional asignado.
     */
    public UsuarioTipo(Usuario usuario, TipoUsuario tipoUsuario) {
        this.usuario = usuario;
        this.tipoUsuario = tipoUsuario;
        this.asignadoEn = OffsetDateTime.now();
    }
}
