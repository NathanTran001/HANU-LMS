package fit.se2.hanulms.repository;

import fit.se2.hanulms.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
