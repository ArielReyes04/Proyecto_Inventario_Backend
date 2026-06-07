package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.Role;
import ec.edu.espe.master.entity.User;
import ec.edu.espe.master.entity.UserRole;
import ec.edu.espe.master.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    boolean existsByRoleAndUser(Role role, User user);
}