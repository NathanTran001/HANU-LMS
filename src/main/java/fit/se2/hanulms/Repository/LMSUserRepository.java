package fit.se2.hanulms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface LMSUserRepository<T, Long> extends JpaRepository<T, Long> {
    Optional<T> findByUsername(String username);

    boolean existsByUsername(String username);
}
