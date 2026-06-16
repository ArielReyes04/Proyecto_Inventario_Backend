package ec.edu.espe.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity Role - Representa los roles del sistema.
 * 
 * Los roles definen los permisos y capacidades que tiene un usuario.
 * Ejemplos: ADMIN, USER, MANAGER, VIEWER, etc.
 * 
 * Cada rol puede estar asociado a múltiples usuarios a través de UserRole.
 * 
 * Estados:
 * - active: Indica si el rol está disponible para asignarse
 * - createdAt/updatedAt: Auditoría de cambios
 */
@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Role {

    /**
     * ID único del rol - Identificador primario.
     * Se genera automáticamente usando UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nombre del rol.
     * Campo único, requerido, máximo 50 caracteres.
     * Ejemplos: "ADMIN", "USER", "MANAGER"
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Descripción del rol.
     * Proporciona contexto sobre las responsabilidades del rol.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Indica si el rol está activo en el sistema.
     * Por defecto es true.
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Colección de asociaciones usuario-rol.
     * Relación inversa: muchos UserRoles se asocian a este rol.
     * - cascade = CascadeType.ALL: Propaga cambios a UserRole
     * - orphanRemoval = true: Elimina UserRoles huérfanos
     * - fetch = FetchType.LAZY: Carga perezosa para optimizar
     */
    @OneToMany(mappedBy = "role", cascade =  CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * Fecha y hora de creación del rol.
     * No puede ser modificado después de la creación.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del rol.
     * Se actualiza automáticamente en cada cambio.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Callback JPA que se ejecuta antes de insertar.
     * Establece automáticamente la fecha de creación.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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
