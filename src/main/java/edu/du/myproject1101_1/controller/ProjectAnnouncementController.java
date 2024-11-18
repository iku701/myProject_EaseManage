package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.ProjectAnnouncement;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.service.ProjectService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ProjectAnnouncementController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @PostMapping("/addAnnouncement")
    public String addAnnouncement(@RequestParam Long projectId, @RequestParam String title, @RequestParam String content, Principal principal, Model model) {
        // 로그인된 사용자 정보 가져오기
        User author = userService.getUserByEmail(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        // 프로젝트 가져오기
        Project project = projectService.getProjectById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        // 팀 리더 확인
        if (!project.getTeamLeader().equals(author)) {
            model.addAttribute("error", "You do not have permission to add announcements.");
            return "view/error/error"; // 권한 오류 페이지로 리다이렉트
        }

        // 새 공지사항 생성
        ProjectAnnouncement announcement = new ProjectAnnouncement();
        announcement.setProject(project);
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setAuthor(author);
        announcement.setCreatedAt(LocalDateTime.now());

        // 공지사항 저장
        projectService.addAnnouncement(announcement);

        // 프로젝트 상세 페이지로 리다이렉트
        return "redirect:/myProjectForm/" + projectId;
    }
}
