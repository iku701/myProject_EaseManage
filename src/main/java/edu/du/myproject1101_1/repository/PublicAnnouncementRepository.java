package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.PublicAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicAnnouncementRepository extends JpaRepository<PublicAnnouncement, Long> {
    // JpaRepository를 상속받으면 findAll(Pageable pageable) 메서드 자동 지원
}
