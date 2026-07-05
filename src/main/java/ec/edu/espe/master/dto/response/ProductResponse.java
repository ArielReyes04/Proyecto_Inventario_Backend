package ec.edu.espe.master.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private String location;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Integer minimumStock;
    private Integer currentStock;
    private Boolean active;
    private CategoryResponse category;
    private ProviderResponse provider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
