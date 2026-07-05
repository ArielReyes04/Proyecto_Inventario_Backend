package ec.edu.espe.master.controller;

import ec.edu.espe.master.dto.request.InventoryMovementRequest;
import ec.edu.espe.master.dto.response.InventoryMovementResponse;
import ec.edu.espe.master.services.InventoryMovementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-movements")
@CrossOrigin(origins = "*")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementService movementService;

    @GetMapping
    public ResponseEntity<List<InventoryMovementResponse>> getAllMovements() {
        return ResponseEntity.ok(movementService.getAllMovements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementResponse> getMovementById(@PathVariable UUID id) {
        return ResponseEntity.ok(movementService.getMovementById(id));
    }

    @PostMapping
    public ResponseEntity<InventoryMovementResponse> createMovement(@Valid @RequestBody InventoryMovementRequest request) {
        return new ResponseEntity<>(movementService.createMovement(request), HttpStatus.CREATED);
    }
}
