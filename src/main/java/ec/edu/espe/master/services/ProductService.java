package ec.edu.espe.master.services;

import ec.edu.espe.master.dto.request.ProductRequest;
import ec.edu.espe.master.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(UUID categoryId);
    ProductResponse getProductById(UUID id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id);
    ProductResponse toggleActive(UUID id);
}
