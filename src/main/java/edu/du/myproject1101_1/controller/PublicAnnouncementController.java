package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.PublicAnnouncementRepository;
import edu.du.myproject1101_1.repository.ProjectRepository;
import edu.du.myproject1101_1.service.ProjectService;
import edu.du.myproject1101_1.service.PublicAnnouncementService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/announcements")
public class PublicAnnouncementController {

    @Autowired
    private PublicAnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    // 공고 리스트 및 등록 폼
    @GetMapping("/notifications")
    public String getNotifications(Model model) {
        List<PublicAnnouncement> announcements = announcementService.getAllAnnouncements();
        model.addAttribute("announcements", announcements);
        return "view/notifications";
    }

    // 공고 등록
    @PostMapping("/postAnnouncement")
    public String postAnnouncement(@RequestParam String title,
                                   @RequestParam String content,
                                   @RequestParam String projectName,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        try {
            // 로그인한 사용자 정보 가져오기
            User postedBy = userService.getUserByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found for email: " + principal.getName()));

            // 프로젝트 이름으로 프로젝트 조회
            Project project = projectService.findByName(projectName)
                    .orElseThrow(() -> new RuntimeException("Project not found: " + projectName));

            // 공고 저장
            announcementService.saveAnnouncement(title, content, projectName, postedBy);

            // 성공 메시지 설정
            redirectAttributes.addFlashAttribute("successMessage", "Announcement posted successfully.");
            return "redirect:/view/notifications";

        } catch (Exception e) {
            // 예외 메시지 로깅
            e.printStackTrace();

            // 오류 메시지 설정
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/view/notifications";
        }
    }

    @GetMapping("/project/{projectId}")
    public String getAnnouncementsByProject(@PathVariable Long projectId, Model model) {
        List<PublicAnnouncement> announcements = announcementService.getAnnouncementsByProject(projectId);
        model.addAttribute("announcements", announcements);
        return "view/notifications/notification";
    }

    @PostMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, Model model) {
        announcementService.deleteAnnouncement(id);
        model.addAttribute("successMessage", "Announcement deleted successfully!");
        return "redirect:/announcements";
    }
}

