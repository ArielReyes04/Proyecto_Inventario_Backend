package ec.edu.espe.master.services;

import ec.edu.espe.master.dto.request.InventoryMovementRequest;
import ec.edu.espe.master.dto.response.InventoryMovementResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementService {
    List<InventoryMovementResponse> getAllMovements();
    InventoryMovementResponse getMovementById(UUID id);
    InventoryMovementResponse createMovement(InventoryMovementRequest request);
}
