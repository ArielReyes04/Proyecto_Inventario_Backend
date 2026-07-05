package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Product> findByActiveTrueAndDeletedFalse();
    List<Product> findByCategoryIdAndActiveTrueAndDeletedFalse(UUID categoryId);
    List<Product> findByProviderIdAndActiveTrueAndDeletedFalse(UUID providerId);
}
