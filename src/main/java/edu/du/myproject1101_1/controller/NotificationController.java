package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.*;
import edu.du.myproject1101_1.repository.UserRepository;
import edu.du.myproject1101_1.service.*;
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
    private final CommunityPostService communityPostService;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CommentService commentService;

    public NotificationController(PublicAnnouncementService announcementService,
                                  ProjectService projectService,
                                  UserRepository userRepository,
                                  UserService userService,
                                  CommentService commentService,
                                  CommunityPostService communityPostService) {
        this.announcementService = announcementService;
        this.communityPostService = communityPostService;
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/view/notifications")
    public String viewNotifications(@RequestParam(defaultValue = "0") int announcementPage,
                                    @RequestParam(defaultValue = "0") int communityPage,
                                    @RequestParam(defaultValue = "5") int announcementSize,
                                    @RequestParam(defaultValue = "6") int communitySize,
                                    Model model, Principal principal) {
        // 로그인된 사용자 가져오기
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        // 사용자가 포함된 프로젝트 가져오기
        List<Project> projects = projectService.getProjectsByUser(currentUser);
        model.addAttribute("projects", projects);

        // 공고문 페이징 처리
        Page<PublicAnnouncement> announcements = announcementService.getPagedAnnouncements(PageRequest.of(announcementPage, announcementSize));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        announcements.getContent().forEach(announcement -> {
            announcement.setFormattedCreatedAt(announcement.getCreatedAt().format(formatter));
        });

        // 자유게시판 페이징 처리
        Page<CommunityPost> communityPosts = communityPostService.getPagedCommunityPosts(PageRequest.of(communityPage, communitySize));
        communityPosts.getContent().forEach(post -> {
            post.setFormattedCreatedAt(post.getCreatedAt().format(formatter));
        });

        // 공고문 및 자유게시판 목록 페이징
        model.addAttribute("announcements", announcements.getContent());
        model.addAttribute("communityPosts", communityPosts.getContent());
        model.addAttribute("announcementCurrentPage", announcementPage);
        model.addAttribute("communityCurrentPage", communityPage);
        model.addAttribute("announcementTotalPages", announcements.getTotalPages());
        model.addAttribute("communityTotalPages", communityPosts.getTotalPages());
        model.addAttribute("announcementPageSize", announcementSize);
        model.addAttribute("communityPageSize", communitySize);

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
        }else if ("COMMUNITY_POST".equalsIgnoreCase(commentType)) {
            // 다른 종류의 댓글인 경우 추가적인 리다이렉트 처리
            return "redirect:/view/notifications/myCommunityPostForm/" + referenceId;
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
        // 현재 로그인한 사용자 정보 확인
        User currentUser = userRepository.findByEmail(principal.getName())
           .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        // 댓글 삭제 처리 (댓글 및 대댓글 포함)
        commentService.deleteComment(commentId, currentUser);

        // 성공 메시지 설정
        redirectAttributes.addFlashAttribute("successMessage", "Comment or reply deleted successfully!");

        // 공고문 페이지로 리다이렉트
        return "redirect:/view/notifications/myPublicAnnouncementForm/" + referenceId;
    }

    @PostMapping("/addCommunityPost")
    public String addCommunityPost(@RequestParam String title,
                                   @RequestParam String content,
                                   Principal principal) {
        // 이메일로 사용자 조회
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // 자유게시글 저장
        communityPostService.addCommunityPost(title, content, currentUser);

        // 알림 페이지로 리다이렉트
        return "redirect:/view/notifications";
    }

    // 자유게시판 상세보기
    @GetMapping("/view/notifications/myCommunityPostForm/{id}")
    public String viewCommunityPostDetail(@PathVariable Long id, Model model, Principal principal) {
        // 현재 로그인된 사용자 가져오기
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        CommunityPost post = communityPostService.getPostById(id);

        if (post == null) {
            throw new RuntimeException("Community Post not found with ID: " + id);
        }

        // 댓글 가져오기
        List<Comment> comments = commentService.getCommentsByReferenceIdAndType(id, "COMMUNITY_POST");

        // 날짜 포맷터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        comments.forEach(comment -> {
            comment.setFormattedCreatedAt(comment.getCreatedAt().format(formatter));

            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                comment.getReplies().forEach(reply -> reply.setFormattedCreatedAt(reply.getCreatedAt().format(formatter)));
            }
        });

        // 본인이 작성한 게시글인지 확인
        boolean isOwner = post.getPostedBy().getId().equals(currentUser.getId());
        model.addAttribute("isOwner", isOwner);

        // 게시글 날짜 포맷
        post.setFormattedCreatedAt(post.getCreatedAt().format(formatter));
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("username", currentUser.getUsername());

        return isOwner ? "view/notifications/myCommunityPostForm" : "view/notifications/viewCommunityPostForm";
    }

    // 자유게시판 글 수정
    @PostMapping("/updateCommunityPost")
    public String updateCommunityPost(@RequestParam Long postId,
                                      @RequestParam String title,
                                      @RequestParam String content,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        communityPostService.updateCommunityPost(postId, title, content, currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully!");
        return "redirect:/view/notifications/myCommunityPostForm/" + postId;
    }

    // 자유게시판 글 삭제
    @PostMapping("/deleteCommunityPost")
    public String deleteCommunityPost(@RequestParam Long postId,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        communityPostService.deleteCommunityPost(postId, currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully!");
        return "redirect:/view/notifications";
    }
}