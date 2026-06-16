package ec.edu.espe.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity UserRole - Tabla de asociación entre usuarios y roles.
 * 
 * Implementa una relación muchos-a-muchos entre User y Role.
 * Permite que:
 * - Un usuario tenga múltiples roles
 * - Un rol sea asignado a múltiples usuarios
 * 
 * Atributos especiales:
 * - active: Permite desactivar un rol sin eliminar el registro (eliminación lógica)
 * - assignedAt: Auditoría para saber cuándo se asignó el rol
 * - updatedAt: Auditoría para cambios posteriores
 * 
 * Clave primaria: Clave compuesta (idUser, idRole) en UserRoleId
 */
@Entity
@Table(name = "user_role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRole {

    /**
     * Clave primaria compuesta.
     * Se compone de:
     * - idUser: ID del usuario
     * - idRole: ID del rol
     * Esto previene duplicados (un usuario no puede tener el mismo rol dos veces).
     */
    @EmbeddedId
    private UserRoleId id;

    /**
     * Referencia a la entidad User.
     * @MapsId("idUser") vincula el ID del usuario en la clave compuesta.
     * FetchType.LAZY optimiza para no cargar usuarios innecesariamente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUser")
    @JoinColumn(name = "id_user")
    private User user;

    /**
     * Referencia a la entidad Role.
     * @MapsId("idRole") vincula el ID del rol en la clave compuesta.
     * FetchType.LAZY optimiza para no cargar roles innecesariamente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idRole")
    @JoinColumn(name = "id_role")
    private Role role;

    /**
     * Indica si esta asignación de rol es activa.
     * Por defecto es true (activo).
     * Si es false, el usuario no tiene ese rol para autenticación/autorización.
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Fecha y hora en que se asignó el rol al usuario.
     * No puede ser modificado después de la asignación.
     */
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    /**
     * Fecha y hora de la última actualización.
     * Se actualiza si el rol es desactivado o reactivado.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Callback JPA que se ejecuta antes de insertar.
     * Establece automáticamente la fecha de asignación.
     */
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }

    /**
     * Callback JPA que se ejecuta antes de actualizar.
     * Actualiza automáticamente la fecha de modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
