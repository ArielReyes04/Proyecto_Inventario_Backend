package ec.edu.espe.master.repository;

import ec.edu.espe.master.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);

    //anthony nestor villarreal macias
    //anvillarrealm
    //andrea nicole villarreal moran
    //anvillarrealm1

    @Query(value = "SELECT * FROM users WHERE username LIKE ('%' || :username || '%')",
            nativeQuery = true)
    List<User> findByLikeUsername(String username);
}
