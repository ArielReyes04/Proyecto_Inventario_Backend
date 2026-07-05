package ec.edu.espe.master.dto.request;

import ec.edu.espe.master.entity.MovementType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class InventoryMovementRequest {
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private MovementType type;

    @Size(max = 50, message = "El número de comprobante no puede tener mas de 50 caracteres")
    private String receiptNumber;

    @NotNull(message = "El usuario que registra el movimiento es obligatorio")
    private UUID userId;

    private UUID providerId;

    private UUID customerId;

    @NotEmpty(message = "El movimiento debe tener al menos un detalle")
    @Valid
    private List<InventoryMovementDetailRequest> details;
}
