package ec.edu.espe.master.controller;

import ec.edu.espe.master.dto.request.InventoryMovementRequest;
import ec.edu.espe.master.dto.response.InventoryMovementResponse;
import ec.edu.espe.master.services.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class InventoryMovementControllerTest {

    @Mock
    private InventoryMovementService inventoryMovementService;

    @InjectMocks
    private InventoryMovementController inventoryMovementController;

    @Test
    void testGetAllMovements() {
        when(inventoryMovementService.getAllMovements()).thenReturn(Collections.emptyList());
        ResponseEntity<List<InventoryMovementResponse>> response = inventoryMovementController.getAllMovements();
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testCreateMovement() {
        InventoryMovementRequest req = mock(InventoryMovementRequest.class);
        InventoryMovementResponse res = mock(InventoryMovementResponse.class);
        when(inventoryMovementService.createMovement(req)).thenReturn(res);
        
        ResponseEntity<InventoryMovementResponse> response = inventoryMovementController.createMovement(req);
        assertEquals(201, response.getStatusCode().value());
    }
}
