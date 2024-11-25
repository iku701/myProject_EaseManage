package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.PublicAnnouncementRepository;
import edu.du.myproject1101_1.repository.ProjectRepository;
import edu.du.myproject1101_1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final PublicAnnouncementRepository announcementRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public NotificationService(PublicAnnouncementRepository announcementRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<PublicAnnouncement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }


    public void saveAnnouncement(String title, String content, Long projectId, String username) {
        System.out.println("Saving announcement: title=" + title + ", content=" + content + ", projectId=" + projectId + ", username=" + username);
        User user = userRepository.findByEmail(username) // 이메일로 사용자 검색
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        PublicAnnouncement announcement = new PublicAnnouncement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setProject(project);
        announcement.setPostedBy(user);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());

        announcementRepository.save(announcement);
    }


}
