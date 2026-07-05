package ec.edu.espe.master.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InventoryMovementDetailResponse {
    private UUID id;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
