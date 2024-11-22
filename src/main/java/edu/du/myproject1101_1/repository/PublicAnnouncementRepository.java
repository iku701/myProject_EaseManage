package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.PublicAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicAnnouncementRepository extends JpaRepository<PublicAnnouncement, Long> {

    // 특정 프로젝트와 관련된 공고문 조회
    @Query("SELECT pa FROM PublicAnnouncement pa WHERE pa.project.projectId = :projectId")
    List<PublicAnnouncement> findByProjectId(@Param("projectId") Long projectId);

    // 제목에 특정 키워드가 포함된 공고문 조회 (검색 기능)
    @Query("SELECT pa FROM PublicAnnouncement pa WHERE pa.title LIKE %:keyword%")
    List<PublicAnnouncement> findByTitleContaining(@Param("keyword") String keyword);

    // 최신 공고문 상위 5개 조회 (날짜 기준 내림차순)
    List<PublicAnnouncement> findTop5ByOrderByCreatedAtDesc();
}
