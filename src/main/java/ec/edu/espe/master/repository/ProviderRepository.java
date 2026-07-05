package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    Optional<Provider> findByDocumentNumber(String documentNumber);
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
    List<Provider> findByActiveTrueAndDeletedFalse();
}
