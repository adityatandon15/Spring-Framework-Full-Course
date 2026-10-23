package in.coderarmy.gatekeeper.management.repository;

import in.coderarmy.gatekeeper.management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
