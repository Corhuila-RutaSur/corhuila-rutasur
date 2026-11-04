package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

import lombok.Getter;

/**
 * Catálogo privado de roles persistentes.
 */
@Entity
@Getter
@Table(name = "rol")
class RolEntidad {
    @Id
    private UUID id;
    private String nombre;
    private boolean activo;
}
