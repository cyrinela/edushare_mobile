package iset.project.GsUser.repositories;

import iset.project.GsUser.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long count();

    List<User> findByRoleNot(String role);
}
