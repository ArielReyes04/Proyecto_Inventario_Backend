package ec.edu.espe.master.service;

import ec.edu.espe.master.dto.request.ProductRequest;
import ec.edu.espe.master.dto.response.ProductResponse;
import ec.edu.espe.master.entity.Category;
import ec.edu.espe.master.entity.Product;
import ec.edu.espe.master.entity.Provider;
import ec.edu.espe.master.repository.CategoryRepository;
import ec.edu.espe.master.repository.ProductRepository;
import ec.edu.espe.master.repository.ProviderRepository;
import ec.edu.espe.master.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testGetAllProducts() {
        when(productRepository.findByActiveTrueAndDeletedFalse()).thenReturn(Collections.emptyList());
        List<ProductResponse> res = productService.getAllProducts();
        assertNotNull(res);
    }
}
