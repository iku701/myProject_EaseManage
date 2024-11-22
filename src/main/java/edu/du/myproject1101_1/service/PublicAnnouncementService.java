package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.PublicAnnouncementRepository;
import edu.du.myproject1101_1.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PublicAnnouncementService {

    @Autowired
    private PublicAnnouncementRepository announcementRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public List<PublicAnnouncement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    // 최신 공고문 5개 조회
    public List<PublicAnnouncement> getRecentAnnouncements() {
        return announcementRepository.findTop5ByOrderByCreatedAtDesc();
    }

    // 특정 프로젝트 관련 공고문 조회
    public List<PublicAnnouncement> getAnnouncementsByProject(Long projectId) {
        return announcementRepository.findByProjectId(projectId);
    }

    // 기존 메서드: 세부 데이터를 인자로 받아 공고 저장
    public PublicAnnouncement saveAnnouncement(String title, String content, String projectName, User postedBy) {
        try {
            // 프로젝트 이름으로 프로젝트 조회
            Project project = projectRepository.findByProjectName(projectName)
                    .orElseThrow(() -> new RuntimeException("Project not found: " + projectName));

            // 공고 생성
            PublicAnnouncement announcement = new PublicAnnouncement();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setCreatedAt(LocalDateTime.now());
            announcement.setUpdatedAt(LocalDateTime.now());
            announcement.setPostedBy(postedBy);
            announcement.setProject(project);

            // 공고 저장
            return announcementRepository.save(announcement);

        } catch (Exception e) {
            // 상세한 예외 메시지 로그 출력
            e.printStackTrace();
            throw new RuntimeException("Failed to save announcement: " + e.getMessage());
        }
    }

    // 새로운 메서드: PublicAnnouncement 객체를 직접 저장
    public PublicAnnouncement saveAnnouncement(PublicAnnouncement announcement) {
        if (announcement.getProject() == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (announcement.getPostedBy() == null) {
            throw new IllegalArgumentException("PostedBy (user) cannot be null");
        }

        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now()); // updatedAt 설정
        return announcementRepository.save(announcement);
    }

    // 공고문 삭제
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

}
