package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.Comment;
import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import edu.du.myproject1101_1.service.CommentService;
import edu.du.myproject1101_1.service.PublicAnnouncementService;
import edu.du.myproject1101_1.service.ProjectService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class NotificationController {

    private final PublicAnnouncementService announcementService;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CommentService commentService;

    public NotificationController(PublicAnnouncementService announcementService,
                                  ProjectService projectService,
                                  UserRepository userRepository,
                                  UserService userService,
                                  CommentService commentService) {
        this.announcementService = announcementService;
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.commentService = commentService;
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

        Page<PublicAnnouncement> announcements = announcementService.getPagedAnnouncements(PageRequest.of(page, size));

        // 날짜 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        announcements.getContent().forEach(announcement -> {
            announcement.setFormattedCreatedAt(announcement.getCreatedAt().format(formatter));
        });

        // 공고 목록 페이징
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

        // 공고가 있는지 확인
        if (announcement == null) {
            throw new RuntimeException("Public Announcement not found with ID: " + id);
        }

        // 댓글 가져오기
        List<Comment> comments = commentService.getCommentsByReferenceIdAndType(id, "PUBLIC_ANNOUNCEMENT");

        // 날짜 포맷터를 미리 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 댓글과 대댓글에 날짜 포맷 설정
        comments.forEach(comment -> {
            comment.setFormattedCreatedAt(comment.getCreatedAt().format(formatter));

            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                comment.getReplies().forEach(reply -> reply.setFormattedCreatedAt(reply.getCreatedAt().format(formatter)));
            }
        });

        // 본인이 작성한 공고문인지 확인
        boolean isOwner = announcement.getPostedBy().getId().equals(currentUser.getId());
        model.addAttribute("isOwner", isOwner);

        // 공고문 날짜 포맷 적용
        announcement.setFormattedCreatedAt(formatter.format(announcement.getCreatedAt()));
        announcement.setFormattedUpdatedAt(
                announcement.getUpdatedAt() != null ? formatter.format(announcement.getUpdatedAt()) : "N/A");

        // 모델에 데이터 추가
        model.addAttribute("announcement", announcement);
        model.addAttribute("comments", comments);
        model.addAttribute("username", currentUser.getUsername()); // 사이드바에 사용자 이름 추가

        // 본인이 작성한 공고문인지에 따라 다른 템플릿 반환
        return isOwner ? "view/notifications/myPublicAnnouncementForm" : "view/notifications/viewPublicAnnouncementForm";
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

    @PostMapping("/addComment")
    public String addComment(@RequestParam String content,
                             @RequestParam Long referenceId,
                             @RequestParam String commentType,
                             @RequestParam(required = false) Long parentCommentId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        // 로그인된 사용자 이메일로 조회
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // 댓글 저장
        commentService.addComment(content, referenceId, commentType, parentCommentId, currentUser.getUsername());

        redirectAttributes.addFlashAttribute("successMessage", "Comment has been added successfully!");

        // 리다이렉트 URL 결정
        if ("PUBLIC_ANNOUNCEMENT".equalsIgnoreCase(commentType)) {
            return "redirect:/view/notifications/myPublicAnnouncementForm/" + referenceId;
        } else {
            // 다른 종류의 댓글인 경우 추가적인 리다이렉트 처리
            return "redirect:/";
        }
    }

    @PostMapping("/updateComment")
    public String updateComment(@RequestParam Long commentId,
                                @RequestParam String content,
                                @RequestParam Long referenceId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        commentService.updateComment(commentId, content, currentUser);

        // 성공 메시지 설정
        redirectAttributes.addFlashAttribute("successMessage", "Comment has been updated successfully!");

        return "redirect:/view/notifications/myPublicAnnouncementForm/" + referenceId;
    }

    @PostMapping("/updateReply")
    public String updateReply(@RequestParam Long commentId,
                              @RequestParam String content,
                              @RequestParam Long referenceId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        commentService.updateComment(commentId, content, currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Reply has been updated successfully!");

        return "redirect:/view/notifications/myPublicAnnouncementForm/" + referenceId;
    }

    @PostMapping("/deleteComment")
    public String deleteComment(@RequestParam Long commentId,
                                @RequestParam Long referenceId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        // 현재 로그인한 사용자 확인
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        // 댓글 삭제 처리
        commentService.deleteComment(commentId, currentUser);

        // 성공 메시지 설정
        redirectAttributes.addFlashAttribute("successMessage", "Comment or reply deleted successfully!");

        // 공고문 페이지로 리다이렉트
        return "redirect:/view/notifications/myPublicAnnouncementForm/" + referenceId;
    }


}