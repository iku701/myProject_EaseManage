package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.service.ProjectService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class OtherProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @GetMapping("/view/otherProjects")
    public String showAllProjects(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "projectName") String sort,
                                  @RequestParam(defaultValue = "asc") String direction,
                                  Model model, Principal principal) {
        Optional<User> userOptional = userService.getUserByEmail(principal.getName());
        if (userOptional.isPresent()) {
            User currentUser = userOptional.get();

            // 정렬 설정
            Sort sorting = Sort.by(Sort.Direction.fromString(direction), sort);
            Pageable pageable = PageRequest.of(page, size, sorting);

            // 프로젝트 데이터 가져오기
            Page<Project> filteredProjects = projectService.getPagedProjectsExcludingUserInvolved(currentUser, pageable);

            model.addAttribute("projects", filteredProjects.getContent());
            model.addAttribute("currentPage", filteredProjects.getNumber());
            model.addAttribute("totalPages", filteredProjects.getTotalPages());
            model.addAttribute("sort", sort);
            model.addAttribute("direction", direction);
            model.addAttribute("username", currentUser.getUsername()); // 사이드바에 표시될 사용자 이름 추가
        } else {
            model.addAttribute("projects", Page.empty().getContent()); // 빈 페이지
            model.addAttribute("errorMessage", "User not found.");
        }
        return "view/otherProjects/otherProjects";
    }

    @GetMapping("/view/otherProjectForm/{id}")
    public String showProjectDetails(@PathVariable Long id, Model model, Principal principal) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());

            // 로그인된 사용자 정보 추가
            Optional<User> user = userService.getUserByEmail(principal.getName());
            user.ifPresent(value -> model.addAttribute("username", value.getUsername()));

            return "view/otherProjects/otherProjectForm"; // otherProjectForm.html 파일 반환
        } else {
            model.addAttribute("errorMessage", "Project not found.");
            return "view/error/error"; // 에러 페이지 반환
        }
    }
}
