package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import edu.du.myproject1101_1.service.PublicAnnouncementService;
import edu.du.myproject1101_1.service.ProjectService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class NotificationController {

    private final PublicAnnouncementService announcementService;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final UserService userService;

    public NotificationController(PublicAnnouncementService announcementService,
                                  ProjectService projectService,
                                  UserRepository userRepository,
                                  UserService userService) {
        this.announcementService = announcementService;
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/view/notifications")
    public String viewNotifications(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    Model model, Principal principal) {
        // 로그인된 사용자 가져오기
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        // 사용자가 포함된 프로젝트 가져오기
        List<Project> projects = projectService.getProjectsByUser(currentUser);
        model.addAttribute("projects", projects);

        // 공고 목록 페이징
        Page<PublicAnnouncement> announcements = announcementService.getPagedAnnouncements(PageRequest.of(page, size));
        model.addAttribute("announcements", announcements.getContent());
        model.addAttribute("currentPage", announcements.getNumber());
        model.addAttribute("totalPages", announcements.getTotalPages());
        model.addAttribute("pageSize", size);

        model.addAttribute("username", currentUser.getUsername()); // 사이드바에 표시될 사용자 이름 추가

        return "view/notifications/notification";
    }




    @PostMapping("/addPublicAnnouncement")
    public String addAnnouncement(@RequestParam String title,
                                  @RequestParam String content,
                                  @RequestParam Long projectId,
                                  Principal principal) {
        // 이메일로 사용자 조회
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // 공지사항 저장
        announcementService.addAnnouncement(title, content, projectId, currentUser);

        // 알림 페이지로 리다이렉트
        return "redirect:/view/notifications";
    }

    @GetMapping("/view/notifications/myPublicAnnouncementForm/{id}")
    public String viewAnnouncementDetail(@PathVariable Long id, Model model, Principal principal) {
        // 현재 로그인된 사용자 가져오기
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        // 공지사항 가져오기
        PublicAnnouncement announcement = announcementService.getAnnouncementById(id);
        model.addAttribute("announcement", announcement);

        // 사이드바에 사용자 이름 추가
        model.addAttribute("username", currentUser.getUsername());

        return "view/notifications/myPublicAnnouncementForm";
    }


    @PostMapping("/updatePublicAnnouncement")
    public String updateAnnouncement(@RequestParam Long id,
                                     @RequestParam String title,
                                     @RequestParam String content,
                                     Principal principal) {
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));
        announcementService.updateAnnouncement(id, title, content, currentUser);
        return "redirect:/view/notifications";
    }

    @PostMapping("/deletePublicAnnouncement")
    public String deleteAnnouncement(@RequestParam Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/view/notifications";
    }

}
