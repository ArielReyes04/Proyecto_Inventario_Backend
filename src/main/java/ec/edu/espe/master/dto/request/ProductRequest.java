package ec.edu.espe.master.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequest {
    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede tener mas de 50 caracteres")
    private String sku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
    private String name;

    private String description;

    @Size(max = 50, message = "La ubicación no puede tener mas de 50 caracteres")
    private String location;

    @NotNull(message = "El precio de costo es obligatorio")
    @Min(value = 0, message = "El precio de costo no puede ser negativo")
    private BigDecimal costPrice;

    @NotNull(message = "El precio de venta es obligatorio")
    @Min(value = 0, message = "El precio de venta no puede ser negativo")
    private BigDecimal salePrice;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minimumStock;

    @NotNull(message = "La categoría es obligatoria")
    private UUID categoryId;

    private UUID providerId;
}
