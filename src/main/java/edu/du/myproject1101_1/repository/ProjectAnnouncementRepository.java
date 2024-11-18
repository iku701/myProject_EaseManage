package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.ProjectAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAnnouncementRepository extends JpaRepository<ProjectAnnouncement, Long> {
}
