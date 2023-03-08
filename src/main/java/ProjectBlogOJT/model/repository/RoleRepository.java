package ProjectBlogOJT.model.repository;

import ProjectBlogOJT.model.entity.ERole;
import ProjectBlogOJT.model.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByRoleName(ERole roleNmae);
}
