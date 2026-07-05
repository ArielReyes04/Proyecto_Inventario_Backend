package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findByActiveTrueAndDeletedFalse();
}
