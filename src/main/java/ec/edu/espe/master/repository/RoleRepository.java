package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    boolean existsByName(String name);
    java.util.Optional<Role> findByName(String name);
}
