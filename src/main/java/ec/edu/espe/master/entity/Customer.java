package ec.edu.espe.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity Customer - Representa un cliente de la ferretería.
 * 
 * Puede ser una empresa o persona natural.
 * Independiente de Person al igual que Provider.
 */
@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * RUC o Cédula del cliente.
     */
    @Column(name = "document_number", nullable = false, unique = true, length = 20)
    private String documentNumber;

    /**
     * Razón social o nombres completos del cliente.
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
