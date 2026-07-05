package ec.edu.espe.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity InventoryMovement - Representa una transacción de inventario (ingreso o egreso).
 * Actúa como cabecera del movimiento.
 */
@Entity
@Table(name = "inventory_movements")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;

    /**
     * Número de comprobante o factura único.
     */
    @Column(name = "receipt_number", unique = true, length = 50)
    private String receiptNumber;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Usuario que registra la acción (Bodeguero, Cajero, etc).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Proveedor, si es un ingreso (compra).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    /**
     * Cliente, si es un egreso (venta).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * Detalles del movimiento de inventario.
     */
    @OneToMany(mappedBy = "movement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryMovementDetail> details = new ArrayList<>();

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
        if (movementDate == null) {
            movementDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
