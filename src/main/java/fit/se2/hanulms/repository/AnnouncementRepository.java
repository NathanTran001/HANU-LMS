package fit.se2.hanulms.repository;

import fit.se2.hanulms.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
