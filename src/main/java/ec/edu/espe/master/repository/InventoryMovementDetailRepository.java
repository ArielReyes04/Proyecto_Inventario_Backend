package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.InventoryMovementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryMovementDetailRepository extends JpaRepository<InventoryMovementDetail, UUID> {
    List<InventoryMovementDetail> findByMovementId(UUID movementId);
    List<InventoryMovementDetail> findByProductId(UUID productId);
}
