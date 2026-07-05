package ec.edu.espe.master.services.impl;

import ec.edu.espe.master.dto.request.ProductRequest;
import ec.edu.espe.master.dto.response.CategoryResponse;
import ec.edu.espe.master.dto.response.ProductResponse;
import ec.edu.espe.master.dto.response.ProviderResponse;
import ec.edu.espe.master.entity.Category;
import ec.edu.espe.master.entity.Product;
import ec.edu.espe.master.entity.Provider;
import ec.edu.espe.master.repository.CategoryRepository;
import ec.edu.espe.master.repository.ProductRepository;
import ec.edu.espe.master.repository.ProviderRepository;
import ec.edu.espe.master.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrueAndDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryIdAndActiveTrueAndDeletedFalse(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya está registrado");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        Provider provider = null;
        if (request.getProviderId() != null) {
            provider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .costPrice(request.getCostPrice())
                .salePrice(request.getSalePrice())
                .minimumStock(request.getMinimumStock())
                .currentStock(0) // Stock inicial es 0, se aumenta con compras
                .category(category)
                .provider(provider)
                .build();

        return mapToResponse(productRepository.save(product));
    }

    @Override
    @PreAuthorize("hasAuthority('Administrador')")
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya está en uso");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        Provider provider = null;
        if (request.getProviderId() != null) {
            provider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
        }

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setLocation(request.getLocation());
        product.setCostPrice(request.getCostPrice());
        product.setSalePrice(request.getSalePrice());
        product.setMinimumStock(request.getMinimumStock());
        product.setCategory(category);
        product.setProvider(provider);

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public ProductResponse toggleActive(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        product.setActive(product.getActive() == null ? false : !product.getActive());
        return mapToResponse(productRepository.save(product));
    }

    private ProductResponse mapToResponse(Product product) {
        CategoryResponse categoryResponse = null;
        if (product.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .build();
        }

        ProviderResponse providerResponse = null;
        if (product.getProvider() != null) {
            providerResponse = ProviderResponse.builder()
                    .id(product.getProvider().getId())
                    .name(product.getProvider().getName())
                    .build();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .location(product.getLocation())
                .costPrice(product.getCostPrice())
                .salePrice(product.getSalePrice())
                .minimumStock(product.getMinimumStock())
                .currentStock(product.getCurrentStock())
                .active(product.getActive())
                .category(categoryResponse)
                .provider(providerResponse)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
