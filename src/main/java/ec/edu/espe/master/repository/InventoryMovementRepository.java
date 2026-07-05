package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.InventoryMovement;
import ec.edu.espe.master.entity.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    Optional<InventoryMovement> findByReceiptNumber(String receiptNumber);
    boolean existsByReceiptNumber(String receiptNumber);
    List<InventoryMovement> findByTypeAndActiveTrueAndDeletedFalse(MovementType type);
    List<InventoryMovement> findByMovementDateBetweenAndActiveTrueAndDeletedFalse(LocalDateTime start, LocalDateTime end);
}
