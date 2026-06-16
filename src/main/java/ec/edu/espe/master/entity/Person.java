package ec.edu.espe.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity Person - Representa la información personal de un usuario.
 * 
 * Esta entidad almacena los datos personales de las personas en el sistema.
 * Es independiente de la autenticación y puede estar asociada a un usuario.
 * 
 * Anotaciones JPA:
 * - @Entity: Indica que esta clase es una entidad JPA mapeada a BD
 * - @Table(name = "persons"): Especifica el nombre de la tabla en BD
 * 
 * Anotaciones Lombok (reducen código boilerplate):
 * - @AllArgsConstructor: Genera constructor con todos los parámetros
 * - @NoArgsConstructor: Genera constructor sin parámetros
 * - @Getter/@Setter: Genera automáticamente getters y setters
 * - @Builder: Genera patrón Builder para instanciación flexible
 */
@Entity
@Table(name = "persons")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Person {

    /**
     * ID único de la persona - Identificador primario.
     * Se genera automáticamente usando UUID (Identificador Único Universal).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * DNI - Documento Nacional de Identidad.
     * Campo único en la BD, máximo 30 caracteres.
     * No puede ser nulo.
     */
    @Column(nullable = false, unique = true, length = 30)
    private String dni;

    /**
     * Primer nombre de la persona.
     * Campo requerido, máximo 30 caracteres.
     */
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    /**
     * Segundo nombre de la persona.
     * Campo requerido, máximo 30 caracteres.
     */
    @Column(name = "middle_name", nullable = false, length = 30)
    private String middleName;

    /**
     * Apellido(s) de la persona.
     * Campo requerido, máximo 30 caracteres.
     */
    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    /**
     * Correo electrónico de la persona.
     * Campo único y requerido, máximo 50 caracteres.
     */
    @Column(nullable = false, length = 50, unique = true)
    private String email;

    /**
     * Número de teléfono de la persona.
     * Campo único y requerido, máximo 15 caracteres.
     */
    @Column(nullable = false, length = 15, unique = true)
    private String phoneNumber;

    /**
     * Dirección de la persona.
     * Campo requerido, sin límite de caracteres (TEXT).
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    /**
     * Indica si la persona está activa en el sistema.
     * Por defecto es true cuando se crea la persona.
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Indica si la persona ha sido eliminada lógicamente del sistema.
     * Por defecto es false (no eliminado).
     * Se utiliza eliminación lógica en lugar de física por auditoría.
     */
    @Builder.Default
    private Boolean deleted = false;

    /**
     * Nacionalidad de la persona.
     * Campo requerido, máximo 30 caracteres.
     */
    @Column(nullable = false,  length = 30)
    private String nationality;

    /**
     * Fecha y hora de creación del registro.
     * No puede ser actualizado una vez creado.
     * Se establece automáticamente en la creación.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del registro.
     * Se actualiza automáticamente cada vez que se modifica la entidad.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Callback JPA que se ejecuta antes de actualizar la entidad.
     * Actualiza automáticamente la fecha de modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
