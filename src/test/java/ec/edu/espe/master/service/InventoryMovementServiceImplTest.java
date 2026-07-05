package ec.edu.espe.master.service;

import ec.edu.espe.master.dto.response.InventoryMovementResponse;
import ec.edu.espe.master.repository.InventoryMovementRepository;
import ec.edu.espe.master.services.impl.InventoryMovementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryMovementServiceImplTest {

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @InjectMocks
    private InventoryMovementServiceImpl inventoryMovementService;

    @Test
    void testGetAllMovements() {
        when(inventoryMovementRepository.findAll()).thenReturn(Collections.emptyList());
        List<InventoryMovementResponse> res = inventoryMovementService.getAllMovements();
        assertNotNull(res);
    }
}
