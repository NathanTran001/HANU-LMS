package fit.se2.hanulms.repository;

import fit.se2.hanulms.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
