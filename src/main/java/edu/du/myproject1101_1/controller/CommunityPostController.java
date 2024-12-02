package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.CommunityPost;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.service.CommunityPostService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/community")
public class CommunityPostController {

    private final CommunityPostService communityPostService;
    private final UserService userService;

    public CommunityPostController(CommunityPostService communityPostService, UserService userService) {
        this.communityPostService = communityPostService;
        this.userService = userService;
    }

    // 자유 게시판 리스트
    @GetMapping
    public String listCommunityPosts(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     Model model) {
        Page<CommunityPost> posts = communityPostService.getPagedCommunityPosts(PageRequest.of(page, size));
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", posts.getNumber());
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("pageSize", size);
        return "view/community/communityPosts"; // 자유게시판 HTML
    }

    // 자유 게시판 글 작성 처리
    @PostMapping("/add")
    public String addCommunityPost(@RequestParam String title,
                                   @RequestParam String content,
                                   Principal principal) {
        User currentUser = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));
        communityPostService.addCommunityPost(title, content, currentUser);
        return "redirect:/community"; // 작성 후 리스트로 리다이렉트
    }
}
