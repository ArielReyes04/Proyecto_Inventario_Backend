package ec.edu.espe.master.controller;

import ec.edu.espe.master.dto.request.ProductRequest;
import ec.edu.espe.master.dto.response.ProductResponse;
import ec.edu.espe.master.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void testGetAllProducts() {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts();
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testCreateProduct() {
        ProductRequest req = mock(ProductRequest.class);
        ProductResponse res = mock(ProductResponse.class);
        when(productService.createProduct(req)).thenReturn(res);
        
        ResponseEntity<ProductResponse> response = productController.createProduct(req);
        assertEquals(201, response.getStatusCode().value());
    }
}
