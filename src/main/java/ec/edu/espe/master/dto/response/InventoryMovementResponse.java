package ec.edu.espe.master.dto.response;

import ec.edu.espe.master.entity.MovementType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InventoryMovementResponse {
    private UUID id;
    private MovementType type;
    private String receiptNumber;
    private LocalDateTime movementDate;
    private BigDecimal total;
    private String userName; // Just the username for simplicity in response
    private ProviderResponse provider;
    private CustomerResponse customer;
    private List<InventoryMovementDetailResponse> details;
    private Boolean active;
    private LocalDateTime createdAt;
}
