package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.ProjectRepository;
import edu.du.myproject1101_1.repository.PublicAnnouncementRepository;
import edu.du.myproject1101_1.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PublicAnnouncementService {

    private final PublicAnnouncementRepository announcementRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public PublicAnnouncementService(PublicAnnouncementRepository announcementRepository,
                                     ProjectRepository projectRepository,
                                     UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // 페이징 처리된 공지사항 가져오기
    public Page<PublicAnnouncement> getPagedAnnouncements(Pageable pageable) {
        return announcementRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    // 공지사항 추가
    public void addAnnouncement(String title, String content, Long projectId, User currentUser) {
        // 프로젝트 찾기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        if (project.getProjectName() == null || project.getProjectName().isEmpty()) {
            throw new RuntimeException("Project name is missing or invalid.");
        }

        // 공지사항 생성 및 저장
        PublicAnnouncement announcement = new PublicAnnouncement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setProject(project);
        announcement.setPostedBy(currentUser);

        announcementRepository.save(announcement);
    }

    // 모든 공지사항 가져오기
    public List<PublicAnnouncement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    // 공지사항 ID로 가져오기
    public PublicAnnouncement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Public Announcement not found with ID: " + id));
    }

    // 공지사항 업데이트
    public void updateAnnouncement(Long id, String title, String content, User currentUser) {
        PublicAnnouncement announcement = getAnnouncementById(id);
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcement.setPostedBy(currentUser);

        announcementRepository.save(announcement);
    }

    // 공지사항 삭제
    public void deleteAnnouncement(Long id) {
        PublicAnnouncement announcement = getAnnouncementById(id);
        announcementRepository.delete(announcement);
    }


}