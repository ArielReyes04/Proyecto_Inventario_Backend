package ec.edu.espe.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity User - Representa las credenciales de autenticación de un usuario.
 * 
 * Esta entidad almacena la información de autenticación e información de sesión.
 * Está vinculada con la entidad Person a través de una relación uno a uno.
 * Cada usuario puede tener múltiples roles a través de la entidad UserRole.
 * 
 * Seguridad:
 * - Las contraseñas se almacenan hasheadas usando BCrypt (60 caracteres)
 * - Nunca se almacenan las contraseñas en texto plano
 * - Se registra la fecha del último login para auditoría
 */
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    /**
     * ID del usuario - Es el mismo ID de la Person asociada.
     * Se usa como clave primaria compartida con Person.
     */
    @Id
    @Column(name = "id_person")
    private UUID id;

    /**
     * Relación uno-a-uno con la entidad Person.
     * @MapsId indica que el ID de User se mapea al ID de Person.
     * FetchType.LAZY optimiza que no se cargue automáticamente.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_person")
    private Person person;

    /**
     * Nombre de usuario único para login.
     * Campo único y requerido, máximo 15 caracteres.
     */
    @Column(unique = true, nullable = false, length = 15)
    private String username;

    /**
     * Hash de la contraseña usando BCrypt.
     * BCrypt produce hashes de 60 caracteres.
     * NUNCA almacena la contraseña en texto plano.
     */
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    /**
     * Fecha y hora del último login del usuario.
     * Se utiliza para auditoría y seguridad.
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * Fecha y hora de creación de la cuenta de usuario.
     * No puede ser modificado después de la creación.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del usuario.
     * Se actualiza automáticamente en cada cambio.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Colección de roles asignados al usuario.
     * Relación uno-a-muchos con UserRole.
     * - cascade = CascadeType.ALL: Propaga cambios a UserRole
     * - orphanRemoval = true: Elimina UserRoles huérfanos
     * - fetch = FetchType.LAZY: Carga perezosa para optimizar rendimiento
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * Callback JPA que se ejecuta antes de insertar la entidad.
     * Establece automáticamente la fecha de creación.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Callback JPA que se ejecuta antes de actualizar la entidad.
     * Actualiza automáticamente la fecha de modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
