package ec.edu.espe.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity Provider - Representa un proveedor de productos.
 * 
 * Puede ser una empresa (RUC y Razón Social) o persona natural.
 * Se mantiene separado de la entidad Person para simplificar el modelo 
 * de negocio de compras y evitar mezclar usuarios con proveedores.
 */
@Entity
@Table(name = "providers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * RUC o Cédula del proveedor.
     */
    @Column(name = "document_number", nullable = false, unique = true, length = 20)
    private String documentNumber;

    /**
     * Razón social o nombres completos.
     */
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
