package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
