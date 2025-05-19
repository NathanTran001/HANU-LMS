package fit.se2.hanulms.Repository;

import fit.se2.hanulms.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
