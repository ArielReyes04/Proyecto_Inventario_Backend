package ec.edu.espe.master.service;

import ec.edu.espe.master.dto.request.InventoryMovementRequest;
import ec.edu.espe.master.dto.request.ProductRequest;
import ec.edu.espe.master.entity.MovementType;
import ec.edu.espe.master.repository.InventoryMovementRepository;
import ec.edu.espe.master.repository.ProductRepository;
import ec.edu.espe.master.services.InventoryMovementService;
import ec.edu.espe.master.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * RoleBasedAccessServiceTest - Pruebas de Integración y Seguridad para RBAC.
 * 
 * Se utiliza @SpringBootTest para levantar el contexto de seguridad de Spring
 * y @MockBean para aislar la capa de persistencia (Base de Datos), permitiendo
 * probar exclusivamente la configuración de seguridad.
 * 
 * NOTA: Para que estas pruebas pasen, los métodos en los servicios deben tener 
 * aplicadas las anotaciones @PreAuthorize("hasRole('...')").
 */
@SpringBootTest
public class RoleBasedAccessServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryMovementService inventoryMovementService;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private InventoryMovementRepository inventoryMovementRepository;

    @BeforeEach
    public void setup() {
        // Limpiamos los mocks antes de cada test porque el DataInitializer
        // ejecuta métodos save() al arrancar el contexto de SpringBootTest.
        Mockito.clearInvocations(productRepository, inventoryMovementRepository);
    }

    // ==========================================
    // A) RESTRICCIONES DEL VENDEDOR / CAJERO
    // ==========================================

    @Test
    @WithMockUser(roles = "VENDEDOR")
    public void registrarEgreso_rolVendedor_success() {
        // Arrange
        InventoryMovementRequest request = new InventoryMovementRequest();
        request.setType(MovementType.EGRESO_VENTA);

        // Act & Assert
        // Evaluamos que la seguridad le permita pasar. Como el mock no está 
        // totalmente configurado, podría lanzar excepciones de negocio (ej. 404),
        // lo importante es que NO lance AccessDeniedException.
        try {
            inventoryMovementService.createMovement(request);
        } catch (AccessDeniedException ex) {
            throw new AssertionError("El Vendedor debería tener permiso para registrar ventas", ex);
        } catch (Exception ignored) {
            // Se ignora cualquier otra excepción (ResponseStatusException, etc.)
        }
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    public void modificarPrecioProducto_rolVendedor_throwsAccessDeniedException() {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest();
        request.setCostPrice(new BigDecimal("10.00"));
        request.setSalePrice(new BigDecimal("15.00"));

        // Act
        // Assert: Validar que intente bloquear la acción
        assertThrows(AccessDeniedException.class, () -> {
            productService.updateProduct(productId, request);
        });

        // Verificamos de forma estricta que el repositorio NUNCA se ejecute 
        // cuando el rol no tenga permisos (Capa de protección).
        verify(productRepository, never()).save(any());
    }

    // ==========================================
    // B) RESTRICCIONES DEL ENCARGADO DE BODEGA
    // ==========================================

    @Test
    @WithMockUser(roles = "ENCARGADO_BODEGA")
    public void modificarPresupuesto_rolBodega_throwsAccessDeniedException() {
        // Nota: Como no existe una entidad de presupuesto actualmente, usamos 
        // la creación/actualización de categorías como ejemplo de gestión restringida.
        
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest();

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            productService.updateProduct(productId, request);
        });

        verify(productRepository, never()).save(any());
    }

    // ==========================================
    // C) RESTRICCIONES DEL ASISTENTE DE COMPRAS
    // ==========================================

    @Test
    @WithMockUser(roles = "ASISTENTE_COMPRAS")
    public void registrarEgresoManual_rolAsistenteCompras_throwsAccessDeniedException() {
        // Arrange
        InventoryMovementRequest request = new InventoryMovementRequest();
        request.setType(MovementType.EGRESO_VENTA);

        // Act & Assert
        // El Asistente no puede registrar salidas de bodega
        assertThrows(AccessDeniedException.class, () -> {
            inventoryMovementService.createMovement(request);
        });

        verify(inventoryMovementRepository, never()).save(any());
    }

    // ==========================================
    // D) PRIVILEGIOS DEL ADMINISTRADOR
    // ==========================================

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void modificarPrecioProducto_rolAdministrador_doesNotThrowAccessDenied() {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest();
        request.setCostPrice(new BigDecimal("12.00"));

        // Act & Assert
        try {
            productService.updateProduct(productId, request);
        } catch (AccessDeniedException ex) {
            throw new AssertionError("El Administrador debe tener acceso irrestricto, NO lanzar AccessDeniedException");
        } catch (Exception ignored) {
            // Cualquier error por datos faltantes del mock es ignorado,
            // pues comprobamos netamente el filtro de seguridad RBAC.
        }
    }
}
