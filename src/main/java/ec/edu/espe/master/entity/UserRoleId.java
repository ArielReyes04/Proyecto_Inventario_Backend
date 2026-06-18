package ec.edu.espe.master.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * UserRoleId - Clave primaria compuesta incrustada para la entidad UserRole.
 * 
 * Implementa Serializable porque es una clave embebida.
 * 
 * Se compone de dos UUID:
 * - idUser: ID del usuario
 * - idRole: ID del rol
 * 
 * Evita que un usuario pueda tener asignado el mismo rol más de una vez.
 * 
 * Ejemplo:
 * - Usuario con ID "abc123" y Rol con ID "role1"
 * - Clave: (abc123, role1)
 * - No se puede repetir esta combinación
 * 
 * @EqualsAndHashCode: Genera equals() y hashCode() basados en los IDs
 * (requerido para claves compuestas en Hibernate)
 */
@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserRoleId implements Serializable {
    /**
     * ID del usuario en la asociación.
     */
    @Column(name = "id_user")
    private UUID idUser;

    /**
     * ID del rol en la asociación.
     */
    @Column(name = "id_role")
    private UUID idRole;
}
